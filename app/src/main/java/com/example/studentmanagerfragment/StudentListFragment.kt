package com.example.studentmanagerfragment

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar

class StudentListFragment : Fragment() {
    private val studentList = mutableListOf(
        StudentModel("Nguyễn Văn An", "SV001"),
        StudentModel("Trần Thị Bảo", "SV002"),
        StudentModel("Lê Hoàng Cường", "SV003"),
        StudentModel("Phạm Thị Dung", "SV004"),
        StudentModel("Đỗ Minh Đức", "SV005"),
        StudentModel("Vũ Thị Hoa", "SV006"),
        StudentModel("Hoàng Văn Hải", "SV007"),
        StudentModel("Bùi Thị Hạnh", "SV008"),
        StudentModel("Đinh Văn Hùng", "SV009"),
        StudentModel("Nguyễn Thị Linh", "SV010"),
        StudentModel("Phạm Văn Long", "SV011"),
        StudentModel("Trần Thị Mai", "SV012"),
        StudentModel("Lê Thị Ngọc", "SV013"),
        StudentModel("Vũ Văn Nam", "SV014"),
        StudentModel("Hoàng Thị Phương", "SV015"),
        StudentModel("Đỗ Văn Quân", "SV016"),
        StudentModel("Nguyễn Thị Thu", "SV017"),
        StudentModel("Trần Văn Tài", "SV018"),
        StudentModel("Phạm Thị Tuyết", "SV019"),
        StudentModel("Lê Văn Vũ", "SV020")
    )

    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.student_list_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listView = view.findViewById(R.id.student_list_view)
        adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            studentList.map { "${it.studentName} (${it.studentId})" }
        )
        listView.adapter = adapter

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Bundle>("studentData")
            ?.observe(viewLifecycleOwner) { result ->
                result?.let { it ->
                    val position = it.getInt("position")
                    val studentName = it.getString("student_name").toString()
                    val studentId = it.getString("student_id").toString()
                    val isEdit = it.getBoolean("is_edit")

                    if (isEdit) {
                        studentList[position] = StudentModel(studentName, studentId)
                    }
                    else {
                        studentList.add(StudentModel(studentName, studentId))
                    }

                    // Update the adapter's data and notify the change
                    adapter.clear()  // Clear the existing data
                    adapter.addAll(studentList.map { "${it.studentName} (${it.studentId})" }) // Add the updated data
                    adapter.notifyDataSetChanged() // Notify the adapter of the change
                }
            }
        registerForContextMenu(listView) // Enable context menu
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        Log.d("OptionMenu", "Option menu is showing")
        inflater.inflate(R.menu.option_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.item_add_std -> {
                val bundle = Bundle().apply {
                    putBoolean("is_edit", false)
                }
                findNavController().navigate(R.id.action_to_addEditFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateContextMenu(
        menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        requireActivity().menuInflater.inflate(R.menu.context_menu, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val info = item.menuInfo as AdapterView.AdapterContextMenuInfo
        val selectedStudent = studentList[info.position]

        return when (item.itemId) {
            R.id.item_edit -> {
                val bundle = Bundle().apply {
                    putInt("position", info.position)
                    putString("student_name", selectedStudent.studentName)
                    putString("student_id", selectedStudent.studentId)
                    putBoolean("is_edit", true)
                }
                findNavController().navigate(R.id.action_to_addEditFragment, bundle)
                true
            }
            R.id.item_remove -> {
                // Store the student to be deleted so it can be restored
                val deletedStudent = selectedStudent
                studentList.removeAt(info.position)

                // Notify the adapter to update the list view
                adapter.clear()  // Clear the existing data
                adapter.addAll(studentList.map { "${it.studentName} (${it.studentId})" }) // Add the updated data
                adapter.notifyDataSetChanged()

                // Show a Snackbar to allow undoing the deletion
                Snackbar.make(requireView(), "Student deleted", Snackbar.LENGTH_LONG)
                    .setAction("Undo") {
                        studentList.add(info.position, deletedStudent)
                        adapter.clear()
                        adapter.addAll(studentList.map { "${it.studentName} (${it.studentId})" })
                        adapter.notifyDataSetChanged()
                        Toast.makeText(requireContext(), "Student restored", Toast.LENGTH_SHORT).show()
                    }
                    .show()
                true
            }
            else -> super.onContextItemSelected(item)
        }
    }
}