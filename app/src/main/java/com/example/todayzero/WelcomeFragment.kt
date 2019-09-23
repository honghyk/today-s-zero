package com.example.todayzero


import android.content.res.ColorStateList
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import com.example.todayzero.db.DBHelper
import com.example.todayzero.db.user
import com.example.todayzero.util.replaceFragInActNotAddToBackStack
import java.text.NumberFormat


class WelcomeFragment : Fragment() {
    lateinit var userNameEditText: EditText
    lateinit var userIncomeEditText: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_welcome, container, false)

        requireActivity().window.decorView.systemUiVisibility = 0
        requireActivity().window.statusBarColor = requireContext().getColor(R.color.colorPrimaryDark)

        val fab = requireActivity().findViewById<FloatingActionButton>(R.id.fab).apply {
            setImageDrawable(requireActivity().getDrawable(R.drawable.ic_arrow_forward_24px))
            backgroundTintList = ColorStateList.valueOf(requireActivity().getColor(R.color.white))
            setOnClickListener {
                val name = userNameEditText.text.toString()
                var income = userIncomeEditText.text.toString()

                if(name.isNotEmpty() && income.isNotEmpty()) {
                    if (income.contains(",")) {
                        income = income.replace(",", "")
                    }
                    val incomeLong = income.toLong()
                    storeUserData(name, incomeLong)

                    (requireActivity() as AppCompatActivity).replaceFragInActNotAddToBackStack(
                        MainFragment(),
                        R.id.main_contentFrame
                    )
                } else {
                    Toast.makeText(requireContext(), "이름과 총급여를 모두 입력해주세요", Toast.LENGTH_SHORT).show()
                }
            }
        }
        with(root) {
            userNameEditText = findViewById(R.id.user_name_edit_text)
            userIncomeEditText = findViewById(R.id.user_income_edit_text)
        }

        showNumberScales()

        return root
    }

    private fun showNumberScales() {
        val watcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                userIncomeEditText.removeTextChangedListener(this)
                var income = s.toString()
                if (income.contains(",")) {
                    income = income.replace(",", "")
                }
                if(income.isNotEmpty()) {
                    val incomeLong = income.toLong()
                    income = NumberFormat.getNumberInstance().format(incomeLong)
                    userIncomeEditText.setText(income)
                    userIncomeEditText.setSelection(income.length)
                }
                userIncomeEditText.addTextChangedListener(this)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        }
        userIncomeEditText.addTextChangedListener(watcher)
    }

    private fun storeUserData(name: String, income: Long) {
        //이름, 총급여 데이터 DB에 넣는 함수
        val dbHelper=DBHelper(requireContext())
        val user= user(name,0,income.toString())
        dbHelper.insertUser(user)

    }
}
