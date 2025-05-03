package br.ifsp.demo.service;

import br.ifsp.demo.dto.RelatorioDTO;
import org.springframework.stereotype.Service;

@Service
public class RelatorioService {

    public RelatorioDTO gerarRelatorioDesempenho() {
        return new RelatorioDTO(100, 2.5, 3500.00, 75.0);
    }
}