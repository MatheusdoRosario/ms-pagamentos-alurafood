package br.com.alurafood.pagamentos.service;

import br.com.alurafood.pagamentos.dto.PagamentoDto;
import br.com.alurafood.pagamentos.http.PedidoClient;
import br.com.alurafood.pagamentos.model.Pagamento;
import br.com.alurafood.pagamentos.model.Status;
import br.com.alurafood.pagamentos.repository.PagamentoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PagamentoService {

    @Autowired
    private PagamentoRepository repository;

    @Autowired
    private ModelMapper mapper;

    @Autowired
    private PedidoClient pedido;

    public Page<PagamentoDto> obterTodos(Pageable pageable) {
        return repository.findAll(pageable)
                .map(p -> mapper.map(p, PagamentoDto.class));
    }

    public PagamentoDto obterPorId(Long id) {
        var pagamento = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pagamento não encontrado"));

        var dto = mapper.map(pagamento, PagamentoDto.class);
        dto.setItens(pedido.obterItensDoPedido(pagamento.getPedidoId()).getItens());
        return dto;
    }

    public PagamentoDto criarPagamento(PagamentoDto dto) {
        var pagamento = mapper.map(dto, Pagamento.class);
        pagamento.setStatus(Status.CRIADO);
        var pagamentoSalvo = repository.save(pagamento);
        return mapper.map(pagamentoSalvo, PagamentoDto.class);
    }

    public PagamentoDto atualizarPagamento(Long id, PagamentoDto dto) {
        var pagamento = mapper.map(dto, Pagamento.class);
        pagamento.setId(id);
        var pagamentoAtualizado = repository.save(pagamento);
        return mapper.map(pagamentoAtualizado, PagamentoDto.class);
    }

    public void excluirPagamento(Long id) {
        repository.deleteById(id);
    }

    public void confirmarPagamento(Long id){
        Optional<Pagamento> pagamento = repository.findById(id);

        if (!pagamento.isPresent()) {
            throw new EntityNotFoundException();
        }

        pagamento.get().setStatus(Status.CONFIRMADO);
        repository.save(pagamento.get());
        pedido.atualizaPagamento(pagamento.get().getPedidoId());
    }

    public void alteraStatus(Long id) {
        Optional<Pagamento> pagamento = repository.findById(id);

        if (!pagamento.isPresent()) {
            throw new EntityNotFoundException();
        }

        pagamento.get().setStatus(Status.CONFIRMADO_SEM_INTEGRACAO);
        repository.save(pagamento.get());
    }
}
