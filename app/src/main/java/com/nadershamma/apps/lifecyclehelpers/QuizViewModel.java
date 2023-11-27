package com.nadershamma.apps.lifecyclehelpers;

import android.arch.lifecycle.ViewModel;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.util.Log;

import com.nadershamma.apps.androidfunwithflags.MainActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class QuizViewModel extends ViewModel {
    private static final String TAG = "FlagQuiz Activity";
    private static final int FLAGS_IN_QUIZ = 3;

    private List<String> fileNameList;
    private List<String> quizCountriesList;
    private Set<String> regionsSet;
    private String correctAnswer;
    private int totalGuesses;
    private int correctAnswers;
    private int guessRows;
    private int point;
    private int correctAnswersAtFirst;
    private boolean isCorrectAtFirst = true;
    private int countOfTry;



    public QuizViewModel() {
        fileNameList = new ArrayList<>();
        quizCountriesList = new ArrayList<>();
    }

    public static String getTag() {
        return TAG;
    }

    public static int getFlagsInQuiz() {
        return FLAGS_IN_QUIZ;
    }

    public int getTotalGuesses() {
        return totalGuesses;
    }

    public void setTotalGuesses(int totalGuesses) {
        this.totalGuesses += totalGuesses;
    }

    public void resetTotalGuesses() {
        this.totalGuesses = 0;
    }

    public List<String> getFileNameList() {
        return fileNameList;
    }

    public void setFileNameList(AssetManager assets) {
        try {
            for (String region : regionsSet) {
                String[] paths = assets.list(region);
                for (String path : paths) {
                    this.fileNameList.add(path.replace(".png", ""));
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "Error loading image file names", e);
        }
    }

    public void clearFileNameList(){
        this.fileNameList.clear();
    }

    public void shuffleFilenameList(){
        Collections.shuffle(this.fileNameList);
        int correct = this.fileNameList.indexOf(this.correctAnswer);
        this.fileNameList.add(this.fileNameList.remove(correct));
    }

    public List<String> getQuizCountriesList() {
        return quizCountriesList;
    }

    public void setQuizCountriesList(List<String> quizCountriesList) {
        this.quizCountriesList = quizCountriesList;
    }

    public void clearQuizCountriesList(){
        this.quizCountriesList.clear();
    }

    public Set<String> getRegionsSet() {
        return regionsSet;
    }

    public void setRegionsSet(Set<String> regions) {
        this.regionsSet = regions;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public String getCorrectCountryName() {
        return correctAnswer.substring(correctAnswer.indexOf('-') + 1)
                .replace('_', ' ');
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public int getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(int correctAnswers) {
        this.correctAnswers += correctAnswers;
    }

    public void resetCorrectAnswers() {
        this.correctAnswers = 0;
    }

    public int getGuessRows() {
        return guessRows;
    }

    public void setGuessRows(String choices) {
        this.guessRows = Integer.parseInt(choices) / 2;
    }

    public String getNextCountryFlag(){
        return quizCountriesList.remove(0);
    }

    public int getCorrectAnswersAtFirst() {
        return  correctAnswersAtFirst;
    }
    public void setCorrectAnswersAtFirst(int correctAnswersAtFirst) {
        if (correctAnswersAtFirst == 0) {
            this.correctAnswersAtFirst = 0;
        }
        else {
            this.correctAnswersAtFirst += correctAnswersAtFirst;
        }

    }

    public int getPoint() {
        return point;
    }
    public void setPoint(int point) {
        this.point += point;
    }

    public boolean getIsCorrectAtFirst() {
        return isCorrectAtFirst;
    }
    public void setIsCorrectAtFirst(boolean isCorrectAtFirst) {
        this.isCorrectAtFirst = isCorrectAtFirst;
    }

    public int getCountOfTry() {
        return countOfTry;
    }
    public void setCountOfTry(int countOfTry) {
        if (countOfTry == 0) {
            this.countOfTry = 0;
        }
        else {
            this.countOfTry += countOfTry;
        }
    }



}
