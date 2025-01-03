package com.ambiasystems.aria.patient.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ambiasystems.aria.patient.model.PatientModel;
import com.ambiasystems.aria.patient.model.PatientModel.ValidCreation;
import com.ambiasystems.aria.patient.model.PatientModel.ValidUpdation;
import com.ambiasystems.aria.patient.service.PatientService;
import com.ambiasystems.aria.patient.util.CommonUtils;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/patient")
@RequiredArgsConstructor
public class PatientController {

	private final PatientService patientService;

	@PostMapping
	public ResponseEntity<?> addPatient(@Validated(ValidCreation.class) @RequestBody PatientModel model,
			BindingResult result) {
		if (result.hasErrors()) {
			return CommonUtils.badRequest(result);
		}
		return ResponseEntity.ok(patientService.addPatient(model));
	}

	@GetMapping("/{patientId}")
	public ResponseEntity<PatientModel> viewPatient(@Positive @PathVariable long patientId) {
		return ResponseEntity.ok(patientService.viewPatient(patientId));
	}

	@PutMapping
	public ResponseEntity<?> updatePatient(@Validated(ValidUpdation.class)@RequestBody PatientModel model,
			BindingResult result) {
		if (result.hasErrors()) {
			return CommonUtils.badRequest(result);
		}
		String email = model.getEmail();
		if (StringUtils.hasText(email) && patientService.isDuplicateEmail(model.getPatientId(), email)) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("Updation Failed : Email already Exists");
		}
		return ResponseEntity.ok(patientService.updatePatient(model));
	}

	@DeleteMapping("/{patientId}")
	public ResponseEntity<Void> deletePatient(@Positive @PathVariable long patientId) {
		patientService.deletePatient(patientId);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/list")
	public ResponseEntity<List<PatientModel>> getAllPatients() {
		return ResponseEntity.ok(patientService.listAllPatients());
	}

	@GetMapping("/page")
	public ResponseEntity<Page<PatientModel>> paginatePatients(@RequestParam(required = true) int pageIndex,
			@Positive @RequestParam(required = true) int pageSize, @RequestParam(required = false) String attributeName,
			@RequestParam(required = false) String sortOrder, @RequestParam(required = false) String patientName) {
		return ResponseEntity
				.ok(patientService.paginatePatients(pageIndex, pageSize, attributeName, sortOrder, patientName));
	}

	@GetMapping("check-duplicate-email/{email}")
	public ResponseEntity<String> isDuplicateEmail(@Email @PathVariable String email) {
		if (patientService.isDuplicateEmail(0,email)) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exists");
		} else {
			return ResponseEntity.ok().body("Available");
		}
	}
}
