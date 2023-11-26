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
        this.mainActivityFragment.getQuizViewModel().setTotalGuesses(1);

        if (guess.equals(answer)) {
            this.mainActivityFragment.getQuizViewModel().setPoint((24/totalGuess) * (totalGuess - this.mainActivityFragment.getQuizViewModel().getCountOfTry()));
            this.mainActivityFragment.getQuizViewModel().setCountOfTry(0);
            Log.e("Correct", this.mainActivityFragment.getQuizViewModel().getPoint() + " ");
            this.mainActivityFragment.getQuizViewModel().setCorrectAnswers(1);
            if (this.mainActivityFragment.getQuizViewModel().getIsCorrectAtFirst()){
                this.mainActivityFragment.getQuizViewModel().setCorrectAnswersAtFirst(1);
            }
            this.mainActivityFragment.getAnswerTextView().setText(answer + "!");
            this.mainActivityFragment.getAnswerTextView().setTextColor(
                    this.mainActivityFragment.getResources().getColor(R.color.correct_answer));

            this.mainActivityFragment.disableButtons();

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
                                mainActivityFragment.animate(true);
                            }
                        }, 2000);
            }
        } else {
            this.mainActivityFragment.getQuizViewModel().setCountOfTry(1);
            this.mainActivityFragment.incorrectAnswerAnimation();
            this.mainActivityFragment.getQuizViewModel().setIsCorrectAtFirst(false);
            guessButton.setEnabled(false);
        }
    }
}
