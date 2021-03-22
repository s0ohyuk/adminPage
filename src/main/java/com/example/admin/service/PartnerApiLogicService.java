package com.example.admin.service;

import com.example.admin.model.entity.Partner;
import com.example.admin.model.network.Header;
import com.example.admin.model.network.request.PartnerApiRequest;
import com.example.admin.model.network.response.PartnerApiResponse;
import com.example.admin.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PartnerApiLogicService extends BaseService<PartnerApiRequest, PartnerApiResponse, Partner>{

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public Header<PartnerApiResponse> create(Header<PartnerApiRequest> request) {

        PartnerApiRequest partnerApiRequest = request.getData();

        Partner partner = Partner.builder()
                .id(partnerApiRequest.getId())
                .name(partnerApiRequest.getName())
                .status(partnerApiRequest.getStatus())
                .address(partnerApiRequest.getAddress())
                .callCenter(partnerApiRequest.getCallCenter())
                .partnerNumber(partnerApiRequest.getPartnerNumber())
                .businessNumber(partnerApiRequest.getBusinessNumber())
                .ceoName(partnerApiRequest.getCeoName())
                .registeredAt(partnerApiRequest.getRegisteredAt())
                .unregisteredAt(partnerApiRequest.getUnregisteredAt())
                .category(categoryRepository.getOne(partnerApiRequest.getId()))
                .build();

        Partner newPartner = baseRepository.save(partner);

        return response(newPartner);
    }

    @Override
    public Header<PartnerApiResponse> read(Long id) {

        return baseRepository.findById(id)
                .map(this::response)
                .orElseGet(()->Header.ERROR("데이터 없음"));
    }

    @Override
    public Header<PartnerApiResponse> update(Header<PartnerApiRequest> request) {

        return Optional.ofNullable(request.getData())
                .map(body -> {
                    return baseRepository.findById(body.getId());
                })
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(partner -> {

                    PartnerApiRequest body = request.getData();
                    partner.setName(body.getName())
                        .setStatus(body.getStatus())
                        .setAddress(body.getAddress())
                        .setCallCenter(body.getCallCenter())
                        .setPartnerNumber(body.getPartnerNumber())
                        .setBusinessNumber(body.getBusinessNumber())
                        .setCeoName(body.getCeoName())
                        .setRegisteredAt(body.getRegisteredAt())
                        .setUnregisteredAt(body.getUnregisteredAt())
                        .setCategory(categoryRepository.getOne(body.getId()));

            return partner;
        })
                .map(changePartner -> baseRepository.save(changePartner))
                .map(newPartner -> response(newPartner))
                .orElseGet(()->Header.ERROR("데이터 없음"));
    }

    @Override
    public Header delete(Long id) {
        return null;
    }

    private Header<PartnerApiResponse> response(Partner partner){

        PartnerApiResponse body = PartnerApiResponse.builder()
                .id(partner.getId())
                .name(partner.getName())
                .status(partner.getStatus())
                .address(partner.getAddress())
                .callCenter(partner.getCallCenter())
                .partnerNumber(partner.getPartnerNumber())
                .businessNumber(partner.getBusinessNumber())
                .ceoName(partner.getCeoName())
                .registeredAt(partner.getRegisteredAt())
                .unregisteredAt(partner.getUnregisteredAt())
                .categoryId(partner.getCategory().getId())
                .build();
        return Header.OK(body);

    }
}
