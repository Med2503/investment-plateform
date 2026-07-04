package org.bank.customerservice.mapper;


import org.bank.customerservice.dto.request.CreateCustomerRequest;
import org.bank.customerservice.dto.response.CustomerResponse;
import org.bank.customerservice.entity.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", source = "userId")
    Customer toEntity(CreateCustomerRequest request, Long userId);


    CustomerResponse toResponse(Customer customer);


}
