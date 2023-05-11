/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */

package com.example.android.unscramble.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.android.unscramble.R
import com.example.android.unscramble.databinding.GameFragmentBinding

/**
 * Fragment where the game is played, contains the game logic.
 */
class GameFragment : Fragment() {

    private var score = 0
    private var currentWordCount = 0
    private var currentWord = "test"
    private var currentScrambledWord = "test"
    private var _words: List<String>? = null
    private val words get() = _words!!

    companion object {
        const val MAX_NO_OF_WORDS = 10
        const val SCORE_INCREASE = 20
        const val KEY_SCORE = "score"
        const val KEY_CURRENT_WORD_COUNT = "currentWordCount"
        const val KEY_CURRENT_WORD = "currentWord"
        const val KEY_CURRENT_SCRAMBLED_WORD = "currentScrambledWord"
    }

    // Binding object instance with access to the views in the game_fragment.xml layout
    private lateinit var binding: GameFragmentBinding

    // Create a ViewModel the first time the fragment is created.
    // If the fragment is re-created, it receives the same GameViewModel instance created by the
    // first fragment

    // Saving application data across Activity lifecycles
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putInt(KEY_SCORE, score)
        outState.putInt(KEY_CURRENT_WORD_COUNT, currentWordCount)
        outState.putString(KEY_CURRENT_WORD, currentWord)
        outState.putString(KEY_CURRENT_SCRAMBLED_WORD, currentScrambledWord)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        savedInstanceState?.let { savedState ->
            score = savedState.getInt(KEY_SCORE, 0)
            currentWordCount = savedState.getInt(KEY_CURRENT_WORD_COUNT, 0)
            currentWord = savedState.getString(KEY_CURRENT_WORD, "test")
            currentScrambledWord = savedState.getString(KEY_CURRENT_SCRAMBLED_WORD, "test")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _words = context?.resources?.getStringArray(R.array.words)?.toList()

        // Inflate the layout XML file and return a binding object instance
        binding = GameFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup a click listener for the Submit and Skip buttons.
        binding.submit.setOnClickListener { onSubmitWord() }
        binding.skip.setOnClickListener { onSkipWord() }

        // Update the UI
        updateNextWordOnScreen()
        binding.score.text = getString(R.string.score, score)
        binding.wordCount.text = getString(
            R.string.word_count, currentWordCount, MAX_NO_OF_WORDS
        )
    }

    /*
    * Checks the user's word, and updates the score accordingly.
    * Displays the next scrambled word.
    */
    private fun onSubmitWord() {
        val usersGuess: String = binding.textInputEditText.text.toString()

        if (usersGuess == currentWord) {
            // User got the scrambled word correct
            score += SCORE_INCREASE
            binding.score.text = getString(R.string.score, score)
            currentScrambledWord = getNextScrambledWord()
            binding.wordCount.text =
                getString(R.string.word_count, currentWordCount, MAX_NO_OF_WORDS)
            setErrorTextField(false)
            updateNextWordOnScreen()
        } else {
            setErrorTextField(true)
        }
    }

    /*
     * Skips the current word without changing the score.
     * Increases the word count.
     */
    private fun onSkipWord() {
        currentScrambledWord = getNextScrambledWord()
        binding.wordCount.text = getString(R.string.word_count, currentWordCount, MAX_NO_OF_WORDS)
        setErrorTextField(false)
        updateNextWordOnScreen()
    }

    /*
     * Increments the currentWordCount and gets a random word
     * from the list of words shuffling the letters within it.
     */
    private fun getNextScrambledWord(): String {
        currentWordCount++

        currentWord = words.random().toString()
        val tempWord = currentWord.toCharArray()
        tempWord.shuffle()
        return String(tempWord)
    }

    /*
     * Exits the game.
     */
    private fun exitGame() {
        activity?.finish()
    }

    /*
    * Sets and resets the text field error status.
    */
    private fun setErrorTextField(error: Boolean) {
        if (error) {
            binding.textField.isErrorEnabled = true
            binding.textField.error = getString(R.string.try_again)
        } else {
            binding.textField.isErrorEnabled = false
            binding.textInputEditText.text = null
        }
    }

    /*
     * Displays the next scrambled word on screen.
     */
    private fun updateNextWordOnScreen() {
        if (currentWordCount == MAX_NO_OF_WORDS)
            exitGame()

        binding.textViewUnscrambledWord.text = currentScrambledWord
    }
}
