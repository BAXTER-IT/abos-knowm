package org.knowm.xchange.kucoin.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SubAccountsResponse {

    private String subUserId;
    private String subName;
    private List<AccountsResponse> mainAccounts;
    private List<AccountsResponse> tradeAccounts;
    private List<AccountsResponse> marginAccounts;
}
