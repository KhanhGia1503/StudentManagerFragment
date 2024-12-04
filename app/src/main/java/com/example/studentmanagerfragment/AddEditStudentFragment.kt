package com.example.studentmanagerfragment

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class AddEditStudentFragment : Fragment(R.layout.add_edit_student_fragment) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etName: EditText = view.findViewById(R.id.et_name)
        val etId: EditText = view.findViewById(R.id.et_id)
        val btnSave: Button = view.findViewById(R.id.btn_save)
        var isEdit: Boolean = false
        var position: Int = 0

        val args = arguments
        if (args != null) {
            position = args.getInt("position")
            etName.setText(args.getString("student_name", ""))
            etId.setText(args.getString("student_id", ""))
            isEdit = args.getBoolean("is_edit")
        }

        btnSave.setOnClickListener {
            val result = Bundle().apply {
                putInt("position", position)
                putString("student_name", etName.text.toString())
                putString("student_id", etId.text.toString())
                putBoolean("is_edit", isEdit)
            }

            // Use the findNavController().previousBackStackEntry to send the result back to the calling fragment
            findNavController().previousBackStackEntry?.savedStateHandle?.set("studentData", result)
            findNavController().popBackStack()
        }
    }
}