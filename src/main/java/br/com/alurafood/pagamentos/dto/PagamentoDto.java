package br.com.alurafood.pagamentos.dto;

import br.com.alurafood.pagamentos.model.Status;

import java.math.BigDecimal;

public record PagamentoDto(
        Long id,
        BigDecimal valor,
        String nome,
        String numero,
        String expiracao,
        String codigo,
        Status status,
        Long formaDePagamentoId,
        Long pedidoId
) {
}
