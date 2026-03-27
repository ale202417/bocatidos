package com.bocaditos.service.partner;

import com.bocaditos.domain.partner.Partner;
import com.bocaditos.dto.partner.PartnerDetailView;
import com.bocaditos.dto.partner.PartnerForm;
import com.bocaditos.dto.partner.PartnerListItem;
import com.bocaditos.repository.ExpenseRepository;
import com.bocaditos.repository.PartnerRepository;
import com.bocaditos.repository.SaleOrderRepository;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional(readOnly = true)
public class PartnerService {

    private final PartnerRepository partnerRepository;
    private final SaleOrderRepository saleOrderRepository;
    private final ExpenseRepository expenseRepository;

    public PartnerService(
            PartnerRepository partnerRepository,
            SaleOrderRepository saleOrderRepository,
            ExpenseRepository expenseRepository
    ) {
        this.partnerRepository = partnerRepository;
        this.saleOrderRepository = saleOrderRepository;
        this.expenseRepository = expenseRepository;
    }

    public List<PartnerListItem> getPartners(String query) {
        List<Partner> partners = StringUtils.hasText(query)
                ? partnerRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrderByLastNameAscFirstNameAsc(query, query)
                : partnerRepository.findAllByOrderByLastNameAscFirstNameAsc();

        return partners.stream()
                .map(this::toListItem)
                .toList();
    }

    public PartnerDetailView getPartner(Long id) {
        Partner partner = partnerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Partner not found"));

        BigDecimal salesTotal = saleOrderRepository.sumTotalAmountByPartnerId(id);
        BigDecimal expenseTotal = expenseRepository.sumAmountByPartnerId(id);

        return new PartnerDetailView(
                partner.getId(),
                partner.getFirstName() + " " + partner.getLastName(),
                partner.getPhone(),
                partner.getEmail(),
                partner.getStatus(),
                partner.getNotes(),
                saleOrderRepository.countByPartnerId(id),
                salesTotal,
                expenseTotal,
                salesTotal.subtract(expenseTotal)
        );
    }

    @Transactional
    public Partner createPartner(PartnerForm form) {
        Partner partner = new Partner();
        partner.setFirstName(form.getFirstName().trim());
        partner.setLastName(form.getLastName().trim());
        partner.setPhone(form.getPhone());
        partner.setEmail(form.getEmail());
        partner.setStatus(form.getStatus());
        partner.setNotes(form.getNotes());
        return partnerRepository.save(partner);
    }

    public PartnerForm emptyForm() {
        return new PartnerForm();
    }

    private PartnerListItem toListItem(Partner partner) {
        Long partnerId = partner.getId();
        return new PartnerListItem(
                partnerId,
                partner.getFirstName() + " " + partner.getLastName(),
                partner.getPhone(),
                partner.getEmail(),
                partner.getStatus(),
                saleOrderRepository.countByPartnerId(partnerId),
                saleOrderRepository.sumTotalAmountByPartnerId(partnerId),
                expenseRepository.sumAmountByPartnerId(partnerId)
        );
    }
}
