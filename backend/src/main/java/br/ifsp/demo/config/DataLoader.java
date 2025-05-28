package br.ifsp.demo.config;

import br.ifsp.demo.model.Estacionamento;
import br.ifsp.demo.repository.EstacionamentoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final EstacionamentoRepository estacionamentoRepository;

    @Override
    public void run(String... args) throws Exception {
        if (estacionamentoRepository.count() == 0) {
            Estacionamento novoEstacionamento = new Estacionamento();

            novoEstacionamento.setNome("Estacionamento Principal Central");
            novoEstacionamento.setCapacidade(200);

            estacionamentoRepository.save(novoEstacionamento);
            System.out.println(">>>> DataLoader: Estacionamento padrão 'Estacionamento Principal Central' foi criado!");
        } else {
            System.out.println(">>>> DataLoader: Pelo menos um estacionamento já existe. Nenhum novo estacionamento padrão foi criado.");
        }
    }
}
