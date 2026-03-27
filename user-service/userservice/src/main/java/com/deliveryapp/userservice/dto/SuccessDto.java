package com.deliveryapp.userservice.dto;

public record SuccessDto<T>(String status,
                            T data) {

}
