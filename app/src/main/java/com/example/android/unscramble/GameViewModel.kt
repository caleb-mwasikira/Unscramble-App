package com.example.android.unscramble

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * [GameViewModel] Holds the data needed for the GameFragment UI
 */
class GameViewModel(_words: List<String>) : ViewModel() {

    private var _score = MutableLiveData(0)
    val score: LiveData<Int> get() = _score

    private var _currentWordCount = MutableLiveData(0)
    val currentWordCount: LiveData<Int> get() = _currentWordCount

    private lateinit var _currentWord: String
    private var _currentScrambledWord = MutableLiveData("test")
    val currentScrambledWord: LiveData<String> get() = _currentScrambledWord

    private val words: List<String> = _words
    private var viewedWords: MutableList<String> = mutableListOf()

    companion object {
        const val MAX_NO_OF_WORDS = 10
        const val SCORE_INCREASE = 20
    }

    init {
        getNextWord()
    }

    private fun getNextWord() {
        _currentWord = words.random()
        if (viewedWords.contains(_currentWord)) {
            getNextWord()
        }

        val tempWord = _currentWord.toCharArray()
        tempWord.shuffle()
        while (String(tempWord) == _currentWord) {
            tempWord.shuffle()
        }
        _currentScrambledWord.value = String(tempWord)
        _currentWordCount.value = (_currentWordCount.value)?.inc()
        viewedWords.add(_currentWord)
    }

    fun nextWord(): Boolean {
        return if (_currentWordCount.value!! < MAX_NO_OF_WORDS) {
            getNextWord()
            true
        } else false
    }

    fun isUsersGuessCorrect(usersGuess: String): Boolean {
        return if (usersGuess == _currentWord) {
            _score.value = (_score.value)?.plus(SCORE_INCREASE)
            true
        } else false
    }

    fun reinitializeData() {
        _score.value = 0
        _currentWordCount.value = 0
        viewedWords.clear()
        getNextWord()
    }
}

/**
 *  The [GameViewModelFactory] class allows us to pass
 *  in custom properties to our GameViewModel
 */
class GameViewModelFactory(private val _words: List<String>): ViewModelProvider.Factory{
    override fun <T:ViewModel> create(modelClass: Class<T>):T{
        if(modelClass.isAssignableFrom(GameViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return GameViewModel(_words) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}