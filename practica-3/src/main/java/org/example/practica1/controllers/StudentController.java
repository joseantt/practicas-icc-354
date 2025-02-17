package org.example.practica1.controllers;

import org.example.practica1.entities.Student;
import org.example.practica1.repositories.StudentRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class StudentController {
    private final StudentRepository studentRepository;

    public StudentController(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @GetMapping("/")
    public String index(Model model, Student student) {
        model.addAttribute(student);
        model.addAttribute("students", studentRepository.findAll());
        return "index";
    }

    @PostMapping("/register-student")
    public String registerStudent(Student student) {
        Student searchedStudent = studentRepository.findById(student.getStudentId()).orElse(null);
        if (searchedStudent != null && searchedStudent.getStudentId().equals(student.getStudentId())) {
            return "redirect:/";
        }

        studentRepository.save(student);
        return "redirect:/";
    }

    @DeleteMapping("/delete-student/{studentId}")
    @ResponseBody
    public void deleteStudent(@PathVariable("studentId") String id) {
        studentRepository.deleteById(id);
    }

    @GetMapping("/edit-student/{studentId}")
    public String editStudent(Model model, @PathVariable("studentId") String id) {
        Student student = studentRepository.findById(id).orElseThrow();
        model.addAttribute("student", student);
        return "fragments/edit-student-form";
    }

    @PostMapping("/update-student/{studentId}")
    public String updateStudent(Student student, @PathVariable("studentId") String id) {
        student.setStudentId(id);
        studentRepository.save(student);
        return "redirect:/";
    }
}
