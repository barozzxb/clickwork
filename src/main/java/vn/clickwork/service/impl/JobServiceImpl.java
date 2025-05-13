package vn.clickwork.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import vn.clickwork.entity.Address;
import vn.clickwork.entity.Employer;
import vn.clickwork.entity.Job;
import vn.clickwork.enumeration.EJobType;
import vn.clickwork.model.Response;
import vn.clickwork.model.dto.AddressDTO;
import vn.clickwork.model.dto.EmployerDTO;
import vn.clickwork.model.dto.EmployerSummaryDTO;
import vn.clickwork.model.dto.JobDTO;
import vn.clickwork.model.dto.JobSummaryDTO;
import vn.clickwork.model.request.JobFilterRequest;
import vn.clickwork.model.response.PageResponse;
import vn.clickwork.repository.JobRepository;
import vn.clickwork.service.JobService;

import java.util.ArrayList;
import java.util.logging.Logger;

@Service
public class JobServiceImpl implements JobService {

	private static final Logger log = Logger.getLogger(JobServiceImpl.class.getName());

	@Autowired
	JobRepository jobRepo;

	@Override
	public ResponseEntity<Response> save(Job entity) {
		try {
			jobRepo.save(entity);
			return new ResponseEntity<Response>(new Response(true, "Cập nhật công việc thành công", entity), HttpStatus.OK);
		} catch (Exception e) {
			log.severe("Lỗi khi lưu công việc: " + e.getMessage());
			return new ResponseEntity<Response>(new Response(false, "Cập nhật công việc thất bại: " + e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public ResponseEntity<Response> findAll() {
		List<Job> jobs = jobRepo.findAll();
		if (jobs.isEmpty()) {
			return new ResponseEntity<Response>(new Response(true, "Danh sách công việc trống", null), HttpStatus.OK);
		}
		List<JobSummaryDTO> jobDTOs = jobs.stream().map(this::mapToSummaryDTO).toList();
		return new ResponseEntity<Response>(new Response(true, "Lấy dữ liệu thành công", jobDTOs), HttpStatus.OK);
	}

	@Override
	public ResponseEntity<Response> findAllPaged(vn.clickwork.model.request.PageRequest pageRequest) {
		Pageable pageable = PageRequest.of(
				pageRequest.getPage(),
				pageRequest.getSize(),
				Sort.by(pageRequest.getSortDir().equalsIgnoreCase("asc") ?
								Sort.Direction.ASC : Sort.Direction.DESC,
						pageRequest.getSortBy())
		);

		Page<Job> page = jobRepo.findAll(pageable);

		List<JobSummaryDTO> content = page.getContent().stream()
				.map(this::mapToSummaryDTO)
				.collect(Collectors.toList());

		PageResponse<JobSummaryDTO> pageResponse = new PageResponse<>(
				content,
				page.getNumber(),
				page.getSize(),
				page.getTotalElements(),
				page.getTotalPages(),
				page.isLast()
		);

		return new ResponseEntity<Response>(
				new Response(true, "Lấy dữ liệu thành công", pageResponse),
				HttpStatus.OK
		);
	}

	@Override
	public ResponseEntity<Response> findById(Long id) {
		Optional<Job> optJob = jobRepo.findById(id);
		if (optJob.isPresent()) {
			JobDTO jobDTO = mapToDTO(optJob.get());
			return new ResponseEntity<Response>(new Response(true, "Lấy dữ liệu thành công", jobDTO), HttpStatus.OK);
		} else {
			return new ResponseEntity<Response>(new Response(false, "Không tìm thấy công việc", null), HttpStatus.NOT_FOUND);
		}
	}

	@Override
	public long count() {
		return jobRepo.count();
	}

	@Override
	public void deleteById(Long id) {
		jobRepo.deleteById(id);
	}

	@Override
	public void delete(Job entity) {
		jobRepo.delete(entity);
	}

	@Override
	public ResponseEntity<Response> findByTags(String tag) {
		List<Job> jobs = jobRepo.findByTags(tag);
		if (jobs.isEmpty()) {
			return new ResponseEntity<Response>(new Response(false, "Không tìm thấy công việc với tag này", null), HttpStatus.NOT_FOUND);
		}
		List<JobSummaryDTO> jobDTOs = jobs.stream().map(this::mapToSummaryDTO).toList();
		return new ResponseEntity<Response>(new Response(true, "Lấy dữ liệu thành công", jobDTOs), HttpStatus.OK);
	}

	@Override
	public ResponseEntity<Response> updateJob(Job jobDetails) {
		Optional<Job> optionalJob = jobRepo.findById(jobDetails.getId());

		if (!optionalJob.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(new Response(false, "Không tìm thấy công việc để cập nhật", null));
		}

		Job existingJob = optionalJob.get();

		existingJob.setName(jobDetails.getName());
		existingJob.setJobtype(jobDetails.getJobtype());
		existingJob.setSalary(jobDetails.getSalary());
		existingJob.setTags(jobDetails.getTags());
		existingJob.setDescription(jobDetails.getDescription());
		existingJob.setRequiredskill(jobDetails.getRequiredskill());
		existingJob.setActive(jobDetails.isActive());
		existingJob.setBenefit(jobDetails.getBenefit());
		existingJob.setField(jobDetails.getField());
		existingJob.setQuantity(jobDetails.getQuantity());

		try {
			Job updatedJob = jobRepo.save(existingJob);
			return ResponseEntity.ok(new Response(true, "Cập nhật công việc thành công", mapToDTO(updatedJob)));
		} catch (Exception e) {
			log.severe("Lỗi khi cập nhật công việc: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new Response(false, "Cập nhật công việc thất bại", null));
		}
	}

	@Override
	public ResponseEntity<Response> findNewJobs() {
		List<Job> newjobs = jobRepo.findAll();
		if (newjobs.isEmpty()) {
			return new ResponseEntity<Response>(new Response(true, "Danh sách công việc trống", null), HttpStatus.OK);
		}
		List<JobSummaryDTO> jobDTOs = newjobs.stream().map(this::mapToSummaryDTO).toList();
		if (jobDTOs.size() > 3) {
			jobDTOs = jobDTOs.subList(0, 3);
		}
		return new ResponseEntity<Response>(new Response(true, "Lấy dữ liệu thành công", jobDTOs), HttpStatus.OK);
	}

	@Override
	public ResponseEntity<Response> filterJobs(JobFilterRequest request) {
		try {
			List<Job> jobs;

			// Chuyển đổi jobType từ String sang EJobType
			EJobType jobType = null;
			if (request.getJobType() != null && !request.getJobType().isBlank()) {
				try {
					jobType = EJobType.valueOf(request.getJobType());
				} catch (IllegalArgumentException e) {
					return new ResponseEntity<>(
							new Response(false, "Loại công việc không hợp lệ: " + request.getJobType(), null),
							HttpStatus.BAD_REQUEST
					);
				}
			}

			// Xử lý filter theo tags nếu có
			if (request.getTags() != null && !request.getTags().isEmpty()) {
				jobs = jobRepo.findByTagsIn(request.getTags());

				// Lọc thêm theo các điều kiện khác
				final EJobType finalJobType = jobType; // Biến final để dùng trong lambda
				jobs = jobs.stream()
						.filter(job -> {
							boolean match = true;

							if (request.getName() != null && !request.getName().isBlank()) {
								match = match && job.getName().toLowerCase().contains(request.getName().toLowerCase());
							}

							if (finalJobType != null) {
								match = match && job.getJobtype() == finalJobType;
							}

							if (request.getEmployerId() != null) {
								match = match && job.getEmployer().getId().equals(request.getEmployerId());
							}

							// Xử lý lọc theo lương
							if (request.getSalaryMin() != null || request.getSalaryMax() != null) {
								try {
									String[] salaryRange = job.getSalary().split("-");
									double minSalary = Double.parseDouble(salaryRange[0].trim());
									double maxSalary = Double.parseDouble(salaryRange[1].trim());

									if (request.getSalaryMin() != null) {
										match = match && minSalary >= request.getSalaryMin();
									}

									if (request.getSalaryMax() != null) {
										match = match && maxSalary <= request.getSalaryMax();
									}
								} catch (Exception e) {
									// Bỏ qua lỗi định dạng lương
								}
							}

							if (request.getIsActive() != null) {
								match = match && job.isActive() == request.getIsActive();
							}

							return match;
						})
						.toList();
			} else {
				// Sử dụng JPQL query nếu không có tags
				jobs = jobRepo.filterJobs(
						request.getName(),
						jobType,
						request.getEmployerId(),
						request.getDateFrom(),
						request.getDateTo(),
						request.getSalaryMin(),
						request.getSalaryMax(),
						request.getIsActive()
				);
			}

			if (jobs.isEmpty()) {
				return new ResponseEntity<>(
						new Response(true, "Không tìm thấy công việc phù hợp", null),
						HttpStatus.OK
				);
			}

			List<JobSummaryDTO> jobDTOs = jobs.stream().map(this::mapToSummaryDTO).toList();
			return new ResponseEntity<>(
					new Response(true, "Lấy dữ liệu thành công", jobDTOs),
					HttpStatus.OK
			);
		} catch (Exception e) {
			log.severe("Lỗi khi lọc công việc: " + e.getMessage());
			return new ResponseEntity<>(
					new Response(false, "Lỗi khi lọc công việc: " + e.getMessage(), null),
					HttpStatus.INTERNAL_SERVER_ERROR
			);
		}
	}

	@Override
	public ResponseEntity<Response> findByEmployerEmail(String email) {
		List<Job> jobs = jobRepo.findByEmployerEmail(email);
		if (jobs.isEmpty()) {
			return ResponseEntity.ok(new Response(true, "Không có công việc nào", List.of()));
		}
		List<JobSummaryDTO> dtos = jobs.stream().map(this::mapToSummaryDTO).toList();
		return ResponseEntity.ok(new Response(true, "Lấy danh sách công việc thành công", dtos));
	}

	@Override
	public ResponseEntity<Response> toggleJobStatus(Long id) {
		Optional<Job> optional = jobRepo.findById(id);
		if (optional.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(new Response(false, "Không tìm thấy công việc", null));
		}

		Job job = optional.get();
		job.setActive(!job.isActive());

		try {
			jobRepo.save(job);
			return ResponseEntity.ok(new Response(true, "Cập nhật trạng thái thành công", job.isActive()));
		} catch (Exception e) {
			log.severe("Lỗi khi cập nhật trạng thái: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new Response(false, "Thất bại khi cập nhật trạng thái", null));
		}
	}

	private JobDTO mapToDTO(Job job) {
		JobDTO dto = new JobDTO();
		dto.setId(job.getId());
		dto.setName(job.getName());
		dto.setJobtype(job.getJobtype() != null ? job.getJobtype().name() : null);
		dto.setCreatedat(job.getCreatedat());
		dto.setSalary(job.getSalary());
		dto.setTags(job.getTags());
		dto.setDescription(job.getDescription());
		dto.setRequiredskill(job.getRequiredskill());
		dto.setBenefit(job.getBenefit());
		dto.setField(job.getField());
		dto.setQuantity(job.getQuantity());
		dto.setActive(job.isActive());

		if (job.getEmployer() != null) {
			dto.setEmployer(mapToEmployerDTO(job.getEmployer()));
		}

		return dto;
	}

	private JobSummaryDTO mapToSummaryDTO(Job job) {
		JobSummaryDTO dto = new JobSummaryDTO();
		dto.setId(job.getId());
		dto.setName(job.getName());
		dto.setJobtype(job.getJobtype() != null ? job.getJobtype().name() : null);
		dto.setCreatedat(job.getCreatedat());
		dto.setSalary(job.getSalary());
		dto.setTags(job.getTags());
		dto.setField(job.getField());
		dto.setActive(job.isActive());

		if (job.getEmployer() != null) {
			dto.setEmployer(mapToEmployerSummaryDTO(job.getEmployer()));
		}

		return dto;
	}

	private EmployerDTO mapToEmployerDTO(Employer employer) {
		EmployerDTO dto = new EmployerDTO();
		dto.setId(employer.getId());
		dto.setFullname(employer.getFullname());
		dto.setEmail(employer.getEmail());

		dto.setPhone(employer.getPhonenum());

		List<AddressDTO> addressDTOs = new ArrayList<>();
		if (employer.getAddresses() != null) {
			for (Address address : employer.getAddresses()) {
				AddressDTO addressDTO = mapToAddressDTO(address);
				addressDTOs.add(addressDTO);
			}
		}
		dto.setAddresses(addressDTOs);

		dto.setLogo(employer.getAvatar());

		dto.setWebsite(employer.getWebsite());
		return dto;
	}

	private EmployerSummaryDTO mapToEmployerSummaryDTO(Employer employer) {
		EmployerSummaryDTO dto = new EmployerSummaryDTO();
		dto.setId(employer.getId());
		dto.setFullname(employer.getFullname());
		dto.setEmail(employer.getEmail());
		dto.setLogo(employer.getAvatar());
		dto.setWebsite(employer.getWebsite());

		if (employer.getAddresses() != null && !employer.getAddresses().isEmpty()) {
			Address mainAddress = employer.getAddresses().get(0);
			dto.setMainAddress(getFullAddressString(mainAddress));
		}

		return dto;
	}

	private AddressDTO mapToAddressDTO(Address address) {
		if (address == null) return null;

		AddressDTO dto = new AddressDTO();
		dto.setId(address.getId());
		dto.setNation(address.getNation());
		dto.setProvince(address.getProvince());
		dto.setDistrict(address.getDistrict());
		dto.setVillage(address.getVillage());
		dto.setDetail(address.getDetail());
		return dto;
	}

	private String getFullAddressString(Address address) {
		if (address == null) return null;

		StringBuilder sb = new StringBuilder();
		if (address.getDetail() != null && !address.getDetail().isEmpty()) {
			sb.append(address.getDetail());
		}
		if (address.getVillage() != null && !address.getVillage().isEmpty()) {
			if (sb.length() > 0) sb.append(", ");
			sb.append(address.getVillage());
		}
		if (address.getDistrict() != null && !address.getDistrict().isEmpty()) {
			if (sb.length() > 0) sb.append(", ");
			sb.append(address.getDistrict());
		}
		if (address.getProvince() != null && !address.getProvince().isEmpty()) {
			if (sb.length() > 0) sb.append(", ");
			sb.append(address.getProvince());
		}
		if (address.getNation() != null && !address.getNation().isEmpty() && !address.getNation().equalsIgnoreCase("Việt Nam")) {
			if (sb.length() > 0) sb.append(", ");
			sb.append(address.getNation());
		}
		return sb.toString();
	}
}