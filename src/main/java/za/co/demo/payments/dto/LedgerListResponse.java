package za.co.demo.payments.dto;

import java.util.List;

public record LedgerListResponse(
    List<LedgerResponse> list) {
}
