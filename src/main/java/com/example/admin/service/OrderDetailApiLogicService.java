package com.example.admin.service;

import com.example.admin.ifs.CrudInterface;
import com.example.admin.model.entity.Category;
import com.example.admin.model.entity.OrderDetail;
import com.example.admin.model.network.Header;
import com.example.admin.model.network.request.CategoryApiRequest;
import com.example.admin.model.network.request.OrderDetailApiRequest;
import com.example.admin.model.network.response.CategoryApiResponse;
import com.example.admin.model.network.response.OrderDetailApiResponse;
import com.example.admin.repository.CategoryRepository;
import com.example.admin.repository.ItemRepository;
import com.example.admin.repository.OrderDetailRepository;
import com.example.admin.repository.OrderGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class OrderDetailApiLogicService extends BaseService<OrderDetailApiRequest, OrderDetailApiResponse, OrderDetail> {

    @Autowired
    private OrderGroupRepository orderGroupRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Override
    public Header<OrderDetailApiResponse> create(Header<OrderDetailApiRequest> request) {

        OrderDetailApiRequest orderDetailApiRequest = request.getData();

        OrderDetail orderDetail = OrderDetail.builder()
                .status(orderDetailApiRequest.getStatus())
                .arrivalDate(LocalDateTime.now().plusDays(2))
                .quantity(orderDetailApiRequest.getQuantity())
                .totalPrice(orderDetailApiRequest.getTotalPrice())
                .orderGroup(orderGroupRepository.getOne(orderDetailApiRequest.getOrderGroupId()))
                .item(itemRepository.getOne(orderDetailApiRequest.getItemId()))
                .build();
        OrderDetail newOrderDetail = baseRepository.save(orderDetail);

        return response(newOrderDetail);
    }

    @Override
    public Header<OrderDetailApiResponse> read(Long id) {

        return baseRepository.findById(id)
                .map(orderDetail -> response(orderDetail))
                .orElseGet(()->Header.ERROR("데이터 없음"));
    }

    @Override
    public Header<OrderDetailApiResponse> update(Header<OrderDetailApiRequest> request) {

        OrderDetailApiRequest orderDetailApiRequest = request.getData();

        Optional<OrderDetail> optional = baseRepository.findById(orderDetailApiRequest.getId());

        return optional.map(orderDetail -> {
            orderDetail.setStatus(orderDetailApiRequest.getStatus())
                    .setArrivalDate(orderDetailApiRequest.getArrivalDate())
                    .setQuantity(orderDetailApiRequest.getQuantity())
                    .setTotalPrice(orderDetailApiRequest.getTotalPrice())
                    .setOrderGroup(orderGroupRepository.getOne(orderDetailApiRequest.getOrderGroupId()))
                    .setItem(itemRepository.getOne(orderDetailApiRequest.getItemId()));
                    return orderDetail;
        })
                .map(orderDetail -> baseRepository.save(orderDetail))
                .map(updateOrderDetail -> response(updateOrderDetail))
                .orElseGet(()->Header.ERROR("데이터 없음"));
    }

    @Override
    public Header delete(Long id) {

        Optional<OrderDetail> optional = baseRepository.findById(id);

        return optional.map(orderDetail -> {
            baseRepository.delete(orderDetail);
            return Header.OK();
        })
                .orElseGet(()->Header.ERROR("데이터 없음"));
    }

    private Header<OrderDetailApiResponse> response(OrderDetail orderDetail){

        OrderDetailApiResponse orderDetailApiResponse = OrderDetailApiResponse.builder()
                .id(orderDetail.getId())
                .status(orderDetail.getStatus())
                .arrivalDate(orderDetail.getArrivalDate())
                .quantity(orderDetail.getQuantity())
                .totalPrice(orderDetail.getTotalPrice())
                .orderGroupId(orderDetail.getOrderGroup().getId())
                .itemId(orderDetail.getItem().getId())
                .build();

        return Header.OK(orderDetailApiResponse);
    }
}
