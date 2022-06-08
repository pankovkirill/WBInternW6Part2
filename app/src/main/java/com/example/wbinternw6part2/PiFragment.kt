package com.example.wbinternw6part2

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.wbinternw6part2.databinding.FragmentPiBinding
import kotlinx.coroutines.*
import java.lang.StringBuilder
import java.lang.Thread.sleep

class PiFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()

    private var pi = ""
    private var counter = START_INDEX

    private var _binding: FragmentPiBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.liveData.observe(viewLifecycleOwner) { renderData(it) }
    }


    private fun renderData(status: String) {
        when (status) {
            "start" -> {
                lifecycleScope.coroutineContext.cancelChildren()
                lifecycleScope.launch {
                    startJob()
                }
            }
            "pause" -> {
                lifecycleScope.coroutineContext.cancelChildren()
            }
            "restart" -> {
                counter = START_INDEX
                pi = ""
                binding.textView.text = "3.1"
                lifecycleScope.coroutineContext.cancelChildren()
                lifecycleScope.launch {
                    startJob()
                }
            }
        }
    }

    private suspend fun startJob() = withContext(Dispatchers.Main) {
        while (true) {
            pi = piCalculated(counter)
            binding.textView.append(pi)
            counter++
            binding.scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            delay(50)
        }
    }


    private fun piCalculated(n: Int): String {
        val pi = StringBuilder(n)
        val boxes = n * 10 / 3
        val reminders = IntArray(boxes)
        for (i in 0 until boxes) {
            reminders[i] = 2
        }
        var heldDigits = 0
        for (i in 0 until n) {
            var carriedOver = 0
            var sum = 0
            for (j in boxes - 1 downTo 0) {
                reminders[j] *= 10
                sum = reminders[j] + carriedOver
                val quotient = sum / (j * 2 + 1)
                reminders[j] = sum % (j * 2 + 1)
                carriedOver = quotient * j
            }
            reminders[0] = sum % 10
            var q = sum / 10

            when (q) {
                9 -> {
                    heldDigits++
                }
                10 -> {
                    q = 0
                    for (k in 1..heldDigits) {
                        var replaced = pi.substring(i - k, i - k + 1).toInt()
                        if (replaced == 9) {
                            replaced = 0
                        } else {
                            replaced++
                        }
                        pi.deleteCharAt(i - k)
                        pi.insert(i - k, replaced)
                    }
                    heldDigits = 1
                }
                else -> {
                    heldDigits = 1
                }
            }
            pi.append(q)
        }
        if (pi.length >= 2) {
            pi.insert(1, '.')
        }
        return pi[pi.length - 1].toString()
    }

    companion object {
        private const val START_INDEX = 3
    }

}