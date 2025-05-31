package br.ifsp.demo.service;

import br.ifsp.demo.model.Pagamento;
import br.ifsp.demo.components.CalculadoraTempoPermanencia;
import br.ifsp.demo.model.Veiculo;
import br.ifsp.demo.exception.PagamentoNaoEncontradoException;
import br.ifsp.demo.exception.VeiculoNaoEncontradoException;
import br.ifsp.demo.repository.PagamentoRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class PagamentoService {

    private final PagamentoRepository pagamentoRepository;
    private final VeiculoService veiculoService;
    private final CalculadoraTempoPermanencia calculadoraTempoPermanencia;

    public void salvarPagamento(Pagamento pagamento) {

        if (pagamento == null)
            throw new IllegalArgumentException("Pagamento não pode ser nulo");

        if (pagamento.getHoraEntrada() == null) {
            throw new IllegalArgumentException("Hora de entrada não pode ser nula");
        }

        if (pagamento.getHoraSaida() == null) {
            throw new IllegalArgumentException("Hora de saída não pode ser nula");
        }

        if (pagamento.getPlaca() == null || pagamento.getPlaca().trim().isEmpty()) {
            throw new IllegalArgumentException("Placa não pode ser nula ou vazia");
        }

        Optional<Veiculo> veiculo = veiculoService.buscarPorPlaca(pagamento.getPlaca());

        if (veiculo.isEmpty()) {
            throw new VeiculoNaoEncontradoException("Veículo não encontrado no estacionamento");
        }

        int horasPermanencia = calcularHorasPermanencia(pagamento.getHoraEntrada(), pagamento.getHoraSaida());
        double valorPermanencia = calculadoraTempoPermanencia.calcularValorDaPermanencia(horasPermanencia);

        pagamento.setValor(valorPermanencia);
        pagamentoRepository.save(pagamento);

        veiculoService.deletarVeiculo(veiculo.get().getId());
    }


    public void deletarPagamento(Pagamento pagamento) {

        if (pagamento == null)
            throw new IllegalArgumentException("Pagamento não pode ser nulo");

        if (pagamentoRepository.findById(pagamento.getUuid()).isEmpty())
            throw new PagamentoNaoEncontradoException("Pagamento não encontrado");

        pagamentoRepository.delete(pagamento);

    }

    public Pagamento atualizarPagamento(UUID uuid, LocalDateTime novaEntrada, LocalDateTime novaSaida, String placa, double novoValor) {
        if (uuid == null)
            throw new IllegalArgumentException("Uuid não pode ser nulo");

        if (novaEntrada == null)
            throw new IllegalArgumentException("Entrada não pode ser nulo");

        if (novaSaida == null)
            throw new IllegalArgumentException("Saída não pode ser nulo");

        if (veiculoService.buscarPorPlaca(placa).isEmpty())
            throw new VeiculoNaoEncontradoException("Veículo não existe no banco de dados");

        Optional<Pagamento> pagamentoOptional = pagamentoRepository.findById(uuid);
        Pagamento pagamento = pagamentoOptional.orElseThrow(() -> new PagamentoNaoEncontradoException("Pagamento não encontrado"));

        pagamento.setHoraEntrada(novaEntrada);
        pagamento.setHoraSaida(novaSaida);
        pagamento.setPlaca(placa);
        pagamento.setValor(novoValor);

        pagamentoRepository.save(pagamento);

        return pagamento;

    }

    public Pagamento buscarPorId(UUID uuid) {

        if (uuid == null)
            throw new IllegalArgumentException("Uuid não pode ser nulo");

        return pagamentoRepository.findById(uuid).orElseThrow(() -> new IllegalArgumentException("Esse pagamento não existe"));
    }

    public List<Pagamento> buscarPorData(LocalDate data) {

        if (data == null)
            throw new IllegalArgumentException("Data não pode ser nula");

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
