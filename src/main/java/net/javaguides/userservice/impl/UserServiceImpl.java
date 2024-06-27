package net.javaguides.userservice.impl;

import lombok.AllArgsConstructor;
import net.javaguides.userservice.dto.DepartmentDto;
import net.javaguides.userservice.dto.ResponseDto;
import net.javaguides.userservice.dto.UserDto;
import net.javaguides.userservice.entity.User;
import net.javaguides.userservice.repository.UserRepository;
import net.javaguides.userservice.service.APIClient;
import net.javaguides.userservice.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private RestTemplate restTemplate;
    private WebClient webClient;
    private APIClient apiClient;

    @Override
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public ResponseDto getUser(Long userId) {
        ResponseDto responseDto = new ResponseDto();
        User user = userRepository.findById(userId).get();
        UserDto userDto = mapToUser(user);

        ResponseEntity<DepartmentDto> responseEntity = restTemplate.getForEntity("http://localhost:8081/api/departments/" + user.getDepartmentId(), DepartmentDto.class);

        // Using REST Template
        DepartmentDto departmentDto = responseEntity.getBody();

        // Using Reactive WebClient
        DepartmentDto departmentDto1 = webClient.get().uri("http://localhost:8081/api/departments/" + user.getDepartmentId()).retrieve().bodyToMono(DepartmentDto.class).block();

        // Using OpenFeign Client
        DepartmentDto departmentDto2 = apiClient.getDepartmentById(Long.valueOf(user.getDepartmentId()));
        System.out.println(responseEntity.getStatusCode());

        responseDto.setUser(userDto);
        // REST TEMPLATE
        // responseDto.setDepartment(departmentDto);
        // WEB CLIENT
        // responseDto.setDepartment(departmentDto1);
        // OPEN FEIGN CLIENT
        responseDto.setDepartment(departmentDto2);

        return responseDto;
    }

    private UserDto mapToUser(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setEmail(user.getEmail());
        return userDto;
    }
}
