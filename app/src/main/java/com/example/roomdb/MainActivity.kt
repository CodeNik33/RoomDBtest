package com.example.roomdb

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.roomdb.databinding.ActivityMainBinding
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {
    //ViewBinding
    private lateinit var binding : ActivityMainBinding
    //AppDatabase reference
    private lateinit var appDb : AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //For ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //!!!
        appDb = AppDatabase.getDatabase(this)

        binding.btnWriteData.setOnClickListener { writeData() }

        binding.btnReadData.setOnClickListener { readData() }

        binding.btnDeleteData.setOnClickListener { deleteData() }

        binding.btnUpdate.setOnClickListener { updateData() }
    }
    @OptIn(DelicateCoroutinesApi::class)
    private fun writeData(){
        val firstName = binding.etFirstName.text.toString()
        val lastName = binding.etLastName.text.toString()
        val rollNo = binding.etRollNo.text.toString()

        if (firstName.isNotEmpty() && lastName.isNotEmpty() && rollNo.isNotEmpty()){
            val student = Student(null, firstName, lastName, rollNo.toInt())
            GlobalScope.launch(Dispatchers.IO){
                appDb.studentDao().insert(student)
            }
            binding.etFirstName.text.clear()
            binding.etLastName.text.clear()
            binding.etRollNo.text.clear()

            Toast.makeText(this, "Successfully written!", Toast.LENGTH_SHORT).show()
        }else {
            Toast.makeText(this, "Please, enter all fields!", Toast.LENGTH_SHORT).show()
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun updateData(){
        val firstName = binding.etFirstName.text.toString()
        val lastName = binding.etLastName.text.toString()
        val rollNo = binding.etRollNo.text.toString()

        if (firstName.isNotEmpty() && lastName.isNotEmpty() && rollNo.isNotEmpty()){
            GlobalScope.launch(Dispatchers.IO){
                appDb.studentDao().update(firstName, lastName, rollNo.toInt())
            }
            binding.etFirstName.text.clear()
            binding.etLastName.text.clear()
            binding.etRollNo.text.clear()

            Toast.makeText(this, "Successfully updated!", Toast.LENGTH_SHORT).show()
        }else {
            Toast.makeText(this, "Please, enter all fields!", Toast.LENGTH_SHORT).show()
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun readData(){
        val rollNo = binding.etRollNoRead.text.toString()
        if (rollNo.isNotEmpty()){
            lateinit var student: Student
            GlobalScope.launch {
                student = appDb.studentDao().findByRoll(rollNo.toInt())
                if (student.rollNo != null){
                    displayData(student)
                } else {
                    Toast.makeText(this@MainActivity, "Student doesn't exist!", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, "Please, enter a roll!", Toast.LENGTH_SHORT).show()
        }
        binding.etRollNoRead.text.clear()
    }

    private suspend fun displayData(student: Student){
        withContext(Dispatchers.Main){
            binding.tvFirstName.text = student.firstName
            binding.tvLastName.text = student.lastName
            binding.tvRollNo.text = student.rollNo.toString()
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun deleteData(){
        GlobalScope.launch(Dispatchers.IO){
            appDb.studentDao().deleteAll()
        }
        Toast.makeText(this, "Database is empty!", Toast.LENGTH_SHORT).show()
    }
}