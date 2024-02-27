package io.github.jlmc.cwr.service.api.market;

import io.github.jlmc.cwr.service.common.TransferRepresentation;
import io.github.jlmc.cwr.service.domain.transfers.TransfersService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/transfers")
public class TransfersController {

    private final TransfersService service;

    public TransfersController(TransfersService service) {
        this.service = service;
    }

    @GetMapping
    public Page<TransferRepresentation> pages(@PageableDefault(size = 10, page = 0) Pageable pageable) {
        return service.getPage(pageable.getPageNumber(), pageable.getPageSize());
    }
}
