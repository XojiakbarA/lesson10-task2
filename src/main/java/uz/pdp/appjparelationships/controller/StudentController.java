package uz.pdp.appjparelationships.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import uz.pdp.appjparelationships.entity.Address;
import uz.pdp.appjparelationships.entity.Group;
import uz.pdp.appjparelationships.entity.Student;
import uz.pdp.appjparelationships.payload.StudentDto;
import uz.pdp.appjparelationships.repository.GroupRepository;
import uz.pdp.appjparelationships.repository.StudentRepository;

import java.util.Optional;

@RestController
@RequestMapping("/student")
public class StudentController {
    @Autowired
    StudentRepository studentRepository;
    @Autowired
    GroupRepository groupRepository;

    //1. VAZIRLIK
    @GetMapping("/forMinistry")
    public Page<Student> getStudentListForMinistry(@RequestParam int page) {
        //1-1=0     2-1=1    3-1=2    4-1=3
        //select * from student limit 10 offset (0*10)
        //select * from student limit 10 offset (1*10)
        //select * from student limit 10 offset (2*10)
        //select * from student limit 10 offset (3*10)
        Pageable pageable = PageRequest.of(page, 10);
        Page<Student> studentPage = studentRepository.findAll(pageable);
        return studentPage;
    }

    //2. UNIVERSITY
    @GetMapping("/forUniversity/{universityId}")
    public Page<Student> getStudentListForUniversity(@PathVariable Integer universityId,
                                                     @RequestParam int page) {
        //1-1=0     2-1=1    3-1=2    4-1=3
        //select * from student limit 10 offset (0*10)
        //select * from student limit 10 offset (1*10)
        //select * from student limit 10 offset (2*10)
        //select * from student limit 10 offset (3*10)
        Pageable pageable = PageRequest.of(page, 10);
        Page<Student> studentPage = studentRepository.findAllByGroup_Faculty_UniversityId(universityId, pageable);
        return studentPage;
    }

    //3. FACULTY DEKANAT
    //4. GROUP OWNER

    @PostMapping
    public String createStudent(@RequestBody StudentDto dto) {
        Optional<Group> group = groupRepository.findById(dto.getGroupId());
        if (!group.isPresent()) {
            return "Error. Group not found with id = " + dto.getGroupId();
        }
        Student student = new Student();
        Address address = new Address();
        address.setCity(dto.getCity());
        address.setDistrict(dto.getDistrict());
        address.setStreet(dto.getStreet());

        student.setFirstName(dto.getFirstName());
        student.setLastName(dto.getLastName());
        student.setAddress(address);
        student.setGroup(group.get());

        Student studentDB = studentRepository.save(student);

        return "Student saved with id = " + studentDB.getId();
    }

    @PutMapping("/{id}")
    public String updateStudent(@RequestBody StudentDto dto, @PathVariable Integer id) {
        Optional<Student> optionalStudent = studentRepository.findById(id);
        if (!optionalStudent.isPresent()) {
            return "Error. Student not found with id = " + id;
        }
        Student student = optionalStudent.get();
        if (dto.getGroupId() != null) {
            Optional<Group> group = groupRepository.findById(dto.getGroupId());
            if (!group.isPresent()) {
                return "Error. Group not found with id = " + dto.getGroupId();
            }
            student.setGroup(group.get());
        }
        Address address = student.getAddress();
        if (dto.getCity() != null) {
            address.setCity(dto.getCity());
        }
        if (dto.getDistrict() != null) {
            address.setDistrict(dto.getDistrict());
        }
        if (dto.getStreet() != null) {
            address.setStreet(dto.getStreet());
        }
        if (dto.getFirstName() != null) {
            student.setFirstName(dto.getFirstName());
        }
        if (dto.getLastName() != null) {
            student.setLastName(dto.getLastName());
        }

        studentRepository.save(student);

        return "Student successfully updated with id = " + id;
    }

    @DeleteMapping("/{id}")
    public String deleteStudent(@PathVariable Integer id) {
        if (studentRepository.existsById(id)) {
            studentRepository.deleteById(id);
            return "Student successfully deleted with id = " + id;
        }
        return "Error. Student not found with id = " + id;
    }
}
