package com.nadershamma.apps.eventhandlers;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.nadershamma.apps.androidfunwithflags.MainActivity;
import com.nadershamma.apps.androidfunwithflags.MainActivityFragment;
import com.nadershamma.apps.androidfunwithflags.R;
import com.nadershamma.apps.androidfunwithflags.ResultsDialogFragment;
import com.nadershamma.apps.lifecyclehelpers.QuizViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import java.util.Random;

public class GuessButtonListener implements OnClickListener {
    private MainActivityFragment mainActivityFragment;
    private MainActivity mainActivity;
    private Handler handler;

    public GuessButtonListener(MainActivityFragment mainActivityFragment) {
        this.mainActivityFragment = mainActivityFragment;
        this.handler = new Handler();
    }

    @Override
    public void onClick(View v) {
        Button guessButton = ((Button) v);
        String guess = guessButton.getText().toString();
        String answer = this.mainActivityFragment.getQuizViewModel().getCorrectCountryName();
        int totalGuess = this.mainActivityFragment.getQuizViewModel().getGuessRows() * 2;
        if (this.mainActivityFragment.getQuizViewModel().getQuestionType() == "flag") {
            this.mainActivityFragment.getQuizViewModel().setTotalGuesses(1);
        }

        if (guess.equals(answer)) {
            boolean isBonus = false;
            if (Objects.equals(this.mainActivityFragment.getQuizViewModel().getQuestionType(), "flag")) {
                this.mainActivityFragment.getQuizViewModel().setPoint((24/totalGuess) * (totalGuess - this.mainActivityFragment.getQuizViewModel().getCountOfTry()));
                this.mainActivityFragment.getQuizViewModel().setCorrectAnswers(1);
            } else {
                this.mainActivityFragment.getQuizViewModel().setPoint(10);
            }
            this.mainActivityFragment.getQuizViewModel().setCountOfTry(0);

            if (this.mainActivityFragment.getQuizViewModel().getIsCorrectAtFirst()){

                JSONObject jsonObject = this.mainActivityFragment.getQuizViewModel().getCapitals(Objects.requireNonNull(this.mainActivityFragment.getContext()), "capitals.json");

                try {
                    if (Objects.equals(this.mainActivityFragment.getQuizViewModel().getQuestionType(), "flag") && jsonObject.getString(guess) != null){
                        this.mainActivityFragment.getQuizViewModel().setCorrectAnswersAtFirst(1);
                        isBonus = true;
                        this.mainActivityFragment.getQuizViewModel().setQuestionType("bonus");
                    }
                    else {
                        isBonus = false;
                        this.mainActivityFragment.getQuizViewModel().setQuestionType("flag");
                    }
                } catch (JSONException e) {
                    this.mainActivityFragment.getQuizViewModel().setCorrectAnswersAtFirst(1);
                    isBonus = false;
                    this.mainActivityFragment.getQuizViewModel().setQuestionType("flag");
                }

            }
            this.mainActivityFragment.getAnswerTextView().setText(answer + "!");
            this.mainActivityFragment.getAnswerTextView().setTextColor(
                    this.mainActivityFragment.getResources().getColor(R.color.correct_answer));

            this.mainActivityFragment.disableButtons();

            if (this.mainActivityFragment.getQuizViewModel().getCorrectAnswers()
                    == QuizViewModel.getFlagsInQuiz() && !isBonus) {
                ResultsDialogFragment quizResults = new ResultsDialogFragment();
                quizResults.setCancelable(false);
                try {
                    quizResults.show(this.mainActivityFragment.getChildFragmentManager(), "Quiz Results");
                } catch (NullPointerException e) {
                    Log.e(QuizViewModel.getTag(),
                            "GuessButtonListener: this.mainActivityFragment.getFragmentManager() " +
                                    "returned null",
                            e);
                }
            } else {
                boolean finalIsBonus = isBonus;
                this.handler.postDelayed(
                        new Runnable() {
                            @Override
                            public void run() {
                                mainActivityFragment.animate(true, finalIsBonus, guess);
                            }
                        }, 1500);
            }
        } else {
            this.mainActivityFragment.incorrectAnswerAnimation();
            if (Objects.equals(this.mainActivityFragment.getQuizViewModel().getQuestionType(), "bonus")) {
                this.mainActivityFragment.getQuizViewModel().setQuestionType("flag");
                this.mainActivityFragment.getAnswerTextView().setText(answer + "!");
                this.mainActivityFragment.getAnswerTextView().setTextColor(
                        this.mainActivityFragment.getResources().getColor(R.color.correct_answer));
                this.mainActivityFragment.disableButtons();
                //this.mainActivityFragment.getQuizViewModel().setCorrectBonusAtFirst(false);
                if (this.mainActivityFragment.getQuizViewModel().getCorrectAnswers()
                        == QuizViewModel.getFlagsInQuiz()) {
                    ResultsDialogFragment quizResults = new ResultsDialogFragment();
                    quizResults.setCancelable(false);
                    try {
                        quizResults.show(this.mainActivityFragment.getChildFragmentManager(), "Quiz Results");
                    } catch (NullPointerException e) {
                        Log.e(QuizViewModel.getTag(),
                                "GuessButtonListener: this.mainActivityFragment.getFragmentManager() " +
                                        "returned null",
                                e);
                    }
                } else {
                    this.handler.postDelayed(
                            new Runnable() {
                                @Override
                                public void run() {
                                    mainActivityFragment.animate(true, false, "");
                                }
                            }, 1500);
                }
            }
            this.mainActivityFragment.getQuizViewModel().setCountOfTry(1);

            this.mainActivityFragment.getQuizViewModel().setIsCorrectAtFirst(false);
            guessButton.setEnabled(false);
        }
    }
}
