package br.ifsp.demo.service;

import br.ifsp.demo.model.Pagamento;
import br.ifsp.demo.model.TempoPermanencia;
import br.ifsp.demo.model.Veiculo;
import br.ifsp.demo.repository.PagamentoRepository;
import br.ifsp.demo.repository.VeiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PagamentoService {

    private final PagamentoRepository pagamentoRepository;
    private final VeiculoService veiculoService;
    private final TempoPermanencia tempoPermanencia;

    @Autowired
    public PagamentoService(PagamentoRepository pagamentoRepository, VeiculoService veiculoService, TempoPermanencia tempoPermanencia) {
        this.pagamentoRepository = pagamentoRepository;
        this.veiculoService = veiculoService;
        this.tempoPermanencia = tempoPermanencia;
    }

    public void salvarPagamento(Pagamento pagamento) {

        if (pagamento == null)
            throw new IllegalArgumentException("Pagamento nao pode ser nulo");

        if(pagamento.getUuid() == null)
            throw new IllegalArgumentException("Pagamento não encontrado");

        if (pagamento.getHoraEntrada() == null)
            throw new IllegalArgumentException("Hora de entrada nao pode ser nula");

        if (pagamento.getHoraSaida() == null)
            throw new IllegalArgumentException("Hora de saida nao pode ser nula");

        if (pagamento.getPlaca() == null || pagamento.getPlaca().trim().isEmpty())
            throw new IllegalArgumentException("Placa nao pode ser nula ou vazia");

        Optional<Veiculo> veiculo = veiculoService.buscarPorPlaca(pagamento.getPlaca());

        if (veiculo.isEmpty())
            throw new IllegalArgumentException("Veiculo nao encontrado no estacionamento");

        int horasPermanencia = calcularHorasPermanencia(pagamento.getHoraEntrada(), pagamento.getHoraSaida());
        double valorPermanencia = tempoPermanencia.calcularValorDaPermanencia(horasPermanencia);
        pagamento.setValor(valorPermanencia);

        pagamentoRepository.save(pagamento);

        veiculoService.deletarVeiculo(veiculo.get().getId());
    }


    private int calcularHorasPermanencia(LocalDateTime horaEntrada, LocalDateTime horaSaida) {

        double horas =java.time.Duration.between(horaEntrada, horaSaida).toMinutes() / 60.0;
        return (int) Math.ceil(horas);

    }

    public void deletarPagamento(Pagamento pagamento) {

        if(pagamento == null)
            throw new IllegalArgumentException("Pagamento nao pode ser nulo");

        if(pagamentoRepository.findById(pagamento.getUuid()).isEmpty())
            throw new IllegalArgumentException("Pagamento nao encontrado");

        pagamentoRepository.delete(pagamento);

    }

    public Pagamento atualizarPagamento(UUID uuid, LocalDateTime novaEntrada, LocalDateTime novaSaida, String placa, double novoValor) {
        if(uuid == null)
            throw new IllegalArgumentException("Uuid nao pode ser nulo");

        if(novaEntrada == null)
            throw new IllegalArgumentException("Entrada nao pode ser nulo");

        if(novaSaida == null)
            throw new IllegalArgumentException("Saida nao pode ser nulo");

        if(veiculoService.buscarPorPlaca(placa).isEmpty())
            throw new IllegalArgumentException("Veiculo nao existe no banco de dados");

        Optional<Pagamento> pagamentoOptional = pagamentoRepository.findById(uuid);
        Pagamento pagamento = pagamentoOptional.orElseThrow(() -> new IllegalArgumentException("Pagamento não encontrado"));

        pagamento.setHoraEntrada(novaEntrada);
        pagamento.setHoraSaida(novaSaida);
        pagamento.setPlaca(placa);
        pagamento.setValor(novoValor);

        pagamentoRepository.save(pagamento);

        return pagamento;

    }

    public Pagamento buscarPorId(UUID uuid) {

        if(uuid == null)
            throw new IllegalArgumentException("Uuid nao pode ser nulo");

        if(pagamentoRepository.findById(uuid).isEmpty())
            throw new IllegalArgumentException("Esse pagamento nao existe");

        return pagamentoRepository.findById(uuid).get();
    }

    public List<Pagamento> buscarPorData(LocalDate data) {

        if(data == null)
            throw new IllegalArgumentException("Data nao pode ser nulo");

        LocalDateTime inicio = data.atStartOfDay();
        LocalDateTime fim = data.atTime(23, 59, 59);

        return pagamentoRepository.findByHoraSaidaBetween(inicio, fim);

    }

    public double calcularTotalArrecadadoPorData(LocalDate data) {
        if (data == null)
            throw new IllegalArgumentException("Data não pode ser nula");

        LocalDateTime inicio = data.atStartOfDay();
        LocalDateTime fim = data.atTime(23, 59, 59);

        Double total = pagamentoRepository.somarPagamentosPorData(inicio, fim);
        return total != null ? total : 0.0;
    }


}
