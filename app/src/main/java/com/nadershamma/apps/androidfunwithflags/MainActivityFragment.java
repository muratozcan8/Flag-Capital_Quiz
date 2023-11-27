package com.nadershamma.apps.androidfunwithflags;

import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.arch.lifecycle.ViewModelProviders;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import android.support.constraint.ConstraintLayout;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.nadershamma.apps.eventhandlers.GuessButtonListener;
import com.nadershamma.apps.lifecyclehelpers.QuizViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivityFragment extends Fragment {

    private SecureRandom random;
    private Animation shakeAnimation;
    private ConstraintLayout quizConstraintLayout;
    private TextView questionNumberTextView;
    private ImageView flagImageView;
    private TableRow[] guessTableRows;
    private TextView answerTextView;
    private QuizViewModel quizViewModel;
    private TextView pointTextView;
    private TextView guessCountryTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.quizViewModel = ViewModelProviders.of(getActivity()).get(QuizViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        OnClickListener guessButtonListener = new GuessButtonListener(this);
        TableLayout answersTableLayout = view.findViewById(R.id.answersTableLayout);

        this.random = new SecureRandom();
        this.shakeAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.incorrect_shake);
        this.shakeAnimation.setRepeatCount(3);
        this.quizConstraintLayout = view.findViewById(R.id.quizConstraintLayout);
        this.questionNumberTextView = view.findViewById(R.id.questionNumberTextView);
        this.flagImageView = view.findViewById(R.id.flagImageView);

        this.guessTableRows = new TableRow[4];
        this.answerTextView = view.findViewById(R.id.answerTextView);
        this.pointTextView = view.findViewById(R.id.textViewPoint);
        this.guessCountryTextView = view.findViewById(R.id.guessCountryTextView);



        for (int i = 0; i < answersTableLayout.getChildCount(); i++) {
            try {
                if (answersTableLayout.getChildAt(i) instanceof TableRow) {
                    this.guessTableRows[i] = (TableRow) answersTableLayout.getChildAt(i);
                }
            } catch (ArrayStoreException e) {
                Log.e(QuizViewModel.getTag(),
                        "Error getting button rows on loop #" + String.valueOf(i), e);
            }
        }

        for (TableRow row : this.guessTableRows) {
            for (int column = 0; column < row.getChildCount(); column++) {
                (row.getChildAt(column)).setOnClickListener(guessButtonListener);
            }
        }

        this.questionNumberTextView.setText(
                getString(R.string.question, 1, QuizViewModel.getFlagsInQuiz()));
        this.pointTextView.setText(
                getString(R.string.point, this.quizViewModel.getPoint())
        );

        return view;
    }

    public void updateGuessRows() {

        int numberOfGuessRows = this.quizViewModel.getGuessRows();
        for (TableRow row : this.guessTableRows) {
            row.setVisibility(View.GONE);
        }
        for (int rowNumber = 0; rowNumber < numberOfGuessRows; rowNumber++) {
            guessTableRows[rowNumber].setVisibility(View.VISIBLE);
        }
    }

    public void resetQuiz() {
        this.quizViewModel.setIsCorrectAtFirst(true);
        this.quizViewModel.setCorrectAnswersAtFirst(0);
        this.quizViewModel.setQuestionType("flag");
        this.quizViewModel.setPoint(-this.quizViewModel.getPoint());
        this.quizViewModel.clearFileNameList();
        this.quizViewModel.setFileNameList(getActivity().getAssets());
        this.quizViewModel.resetTotalGuesses();
        this.quizViewModel.resetCorrectAnswers();
        this.quizViewModel.clearQuizCountriesList();

        int flagCounter = 1;
        int numberOfFlags = this.quizViewModel.getFileNameList().size();
        while (flagCounter <= QuizViewModel.getFlagsInQuiz()) {
            int randomIndex = this.random.nextInt(numberOfFlags);

            String filename = this.quizViewModel.getFileNameList().get(randomIndex);

            if (!this.quizViewModel.getQuizCountriesList().contains(filename)) {
                this.quizViewModel.getQuizCountriesList().add(filename);
                ++flagCounter;
            }
        }

        this.updateGuessRows();
        this.loadNextFlag();
    }

    private void loadNextFlag() {
        this.pointTextView.setText(
            getString(R.string.point, this.quizViewModel.getPoint())
        );
        this.guessCountryTextView.setText(
                getString(R.string.guess_country)
        );
        AssetManager assets = getActivity().getAssets();
        String nextImage = this.quizViewModel.getNextCountryFlag();
        String region = nextImage.substring(0, nextImage.indexOf('-'));

        this.quizViewModel.setCurrentImage(nextImage);

        this.quizViewModel.setIsCorrectAtFirst(true);

        this.quizViewModel.setCorrectAnswer(nextImage);
        answerTextView.setText("");

        questionNumberTextView.setText(getString(R.string.question,
                (quizViewModel.getCorrectAnswers() + 1), QuizViewModel.getFlagsInQuiz()));

        try (InputStream stream = assets.open(region + "/" + nextImage + ".png")) {
            Drawable flag = Drawable.createFromStream(stream, nextImage);
            flagImageView.setImageDrawable(flag);
            animate(false, false, "");
        } catch (IOException e) {
            Log.e(QuizViewModel.getTag(), "Error Loading " + nextImage, e);
        }

        this.quizViewModel.shuffleFilenameList();

        for (int rowNumber = 0; rowNumber < this.quizViewModel.getGuessRows(); rowNumber++) {
            for (int column = 0; column < guessTableRows[rowNumber].getChildCount(); column++) {
                Button guessButton = (Button) guessTableRows[rowNumber].getVirtualChildAt(column);
                guessButton.setEnabled(true);
                String filename = this.quizViewModel.getFileNameList()
                        .get((rowNumber * 2) + column)
                        .substring(this.quizViewModel.getFileNameList()
                                .get((rowNumber * 2) + column).indexOf('-') + 1)
                        .replace('_', ' ');
                guessButton.setText(filename);
            }
        }

        int row = this.random.nextInt(this.quizViewModel.getGuessRows());
        int column = this.random.nextInt(2);
        TableRow randomRow = guessTableRows[row];
        ((Button) randomRow.getChildAt(column)).setText(this.quizViewModel.getCorrectCountryName());
    }

    private void loadBonus(String guess) {
        this.pointTextView.setText(
                getString(R.string.point, this.quizViewModel.getPoint())
        );
        this.guessCountryTextView.setText(
                getString(R.string.guess_capital, guess)
        );

        String correctCapital = "";
        AssetManager assets = getActivity().getAssets();
        JSONObject jsonObject = this.quizViewModel.getCapitals(Objects.requireNonNull(this.getContext()), "capitals.json");
        JSONArray jsonArray = this.quizViewModel.getCapitalList(Objects.requireNonNull(this.getContext()), "capitals.json");
        List<String> chocies = new ArrayList<>();
        try{
            correctCapital = jsonObject.getString(guess);
            chocies.add(correctCapital);
            if(jsonObject.isNull(jsonObject.getString(guess))){
                Log.e("Country", "Capital of " + guess + ": " + jsonObject.getString(guess));

                for (int i = 0; i < this.quizViewModel.getGuessRows() * 2; i++) {
                    Random random = new Random();
                    int randomNumber = random.nextInt(jsonArray.length()-1);
                    String capital = jsonArray.getString(randomNumber);
                    if (!chocies.contains(capital)) {
                        chocies.add(capital);
                    } else {
                        i--;
                    }
                }
            }
            this.quizViewModel.setCorrectAnswer(jsonObject.getString(guess));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String nextImage = this.quizViewModel.getCurrentImage();
        String region = nextImage.substring(0, nextImage.indexOf('-'));

        this.quizViewModel.setIsCorrectAtFirst(true);

        answerTextView.setText("");

        questionNumberTextView.setText(getString(R.string.bonusQuestion));

        try (InputStream stream = assets.open(region + "/" + nextImage + ".png")) {
            Drawable flag = Drawable.createFromStream(stream, nextImage);
            flagImageView.setImageDrawable(flag);
            animate(false, false, "");
        } catch (IOException e) {
            Log.e(QuizViewModel.getTag(), "Error Loading " + nextImage, e);
        }

        for (int rowNumber = 0; rowNumber < this.quizViewModel.getGuessRows(); rowNumber++) {
            for (int column = 0; column < guessTableRows[rowNumber].getChildCount(); column++) {
                Button guessButton = (Button) guessTableRows[rowNumber].getVirtualChildAt(column);
                guessButton.setEnabled(true);
                guessButton.setText(chocies.get(rowNumber*guessTableRows[rowNumber].getChildCount() + column));
            }
        }

        int row = this.random.nextInt(this.quizViewModel.getGuessRows());
        int column = this.random.nextInt(2);
        TableRow randomRow = guessTableRows[row];
        ((Button) randomRow.getChildAt(column)).setText(correctCapital);
        ((Button) guessTableRows[0].getChildAt(0)).setText(chocies.get(row * 2 + column));
    }

    public void animate(boolean animateOut, boolean isBonus, String guess) {
        if (this.quizViewModel.getCorrectAnswers() == 0) {
            return;
        }
        int centreX = (quizConstraintLayout.getLeft() + quizConstraintLayout.getRight()) / 2;
        int centreY = (quizConstraintLayout.getTop() + quizConstraintLayout.getBottom()) / 2;
        int radius = Math.max(quizConstraintLayout.getWidth(), quizConstraintLayout.getHeight());
        Animator animator;
        if (animateOut) {
            animator = ViewAnimationUtils.createCircularReveal(
                    quizConstraintLayout, centreX, centreY, radius, 0);
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    if(isBonus) {
                        loadBonus(guess);
                    }else {
                        loadNextFlag();
                    }

                }
            });
        } else {
            animator = ViewAnimationUtils.createCircularReveal(
                    quizConstraintLayout, centreX, centreY, 0, radius);
        }

        animator.setDuration(500);
        animator.start();
    }

    public void incorrectAnswerAnimation(){
        flagImageView.startAnimation(shakeAnimation);

        answerTextView.setText(R.string.incorrect_answer);
        answerTextView.setTextColor(getResources().getColor(R.color.wrong_answer));
    }

    public void disableButtons() {
        for (TableRow row : this.guessTableRows) {
            for (int column = 0; column < row.getChildCount(); column++) {
                (row.getChildAt(column)).setEnabled(false);
            }
        }
    }

    public TextView getAnswerTextView() {
        return answerTextView;
    }

    public QuizViewModel getQuizViewModel() {
        return quizViewModel;
    }
}

