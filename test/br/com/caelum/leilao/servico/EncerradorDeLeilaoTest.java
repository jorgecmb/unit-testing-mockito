package br.com.caelum.leilao.servico;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.inOrder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.junit.Test;
import org.mockito.InOrder;

import br.com.caelum.leilao.builder.CriadorDeLeilao;
import br.com.caelum.leilao.dominio.Leilao;
import br.com.caelum.leilao.infra.dao.LeilaoDao;
import br.com.caelum.leilao.infra.dao.RepositorioDeLeiloes;

public class EncerradorDeLeilaoTest {
	
	@Test
	public void deveEncerrarLeiloesComMaisDeUmaSemana() {
		Calendar antiga = Calendar.getInstance();
		antiga.set(1999, 1, 20);

        Leilao leilao1 = new CriadorDeLeilao().para("TV de plasma")
            .naData(antiga).constroi();
        Leilao leilao2 = new CriadorDeLeilao().para("Geladeira")
            .naData(antiga).constroi();
        
        List<Leilao> retorno = Arrays.asList(leilao1, leilao2);
        
        RepositorioDeLeiloes daoFalso = mock(RepositorioDeLeiloes.class);
        EnviadorDeEmail enviadorFalso = mock(EnviadorDeEmail.class);
        when(daoFalso.correntes()).thenReturn(retorno);
        
        EncerradorDeLeilao encerrador = new EncerradorDeLeilao(daoFalso, enviadorFalso);
        encerrador.encerra();

        assertEquals(2, encerrador.getTotalEncerrados());
        assertTrue(leilao1.isEncerrado());
        assertTrue(leilao2.isEncerrado());
	}
	
	@Test
	public void naoDeveEncerrarLeiloesComMenosDeUmaSemana() {
		Calendar ontem = Calendar.getInstance();
		ontem.add(Calendar.DAY_OF_MONTH, -1);

        Leilao leilao1 = new CriadorDeLeilao().para("TV de plasma")
            .naData(ontem).constroi();
        Leilao leilao2 = new CriadorDeLeilao().para("Geladeira")
            .naData(ontem).constroi();
        
        List<Leilao> retorno = Arrays.asList(leilao1, leilao2);
        
        RepositorioDeLeiloes daoFalso = mock(RepositorioDeLeiloes.class);
        EnviadorDeEmail enviadorFalso = mock(EnviadorDeEmail.class);
        when(daoFalso.correntes()).thenReturn(retorno);
        
        EncerradorDeLeilao encerrador = new EncerradorDeLeilao(daoFalso, enviadorFalso);
        encerrador.encerra();

        assertEquals(0, encerrador.getTotalEncerrados());
        assertFalse(leilao1.isEncerrado());
        assertFalse(leilao2.isEncerrado());
	}
	
	@Test
	public void naoFazNadaCasoNaoHajaLeilao() {
		RepositorioDeLeiloes daoFalso = mock(RepositorioDeLeiloes.class);
		EnviadorDeEmail enviadorFalso = mock(EnviadorDeEmail.class);
        when(daoFalso.correntes()).thenReturn(new ArrayList<Leilao>());
        
        EncerradorDeLeilao encerrador = new EncerradorDeLeilao(daoFalso, enviadorFalso);
        encerrador.encerra();

        assertEquals(0, encerrador.getTotalEncerrados());
	}
	
	@Test
	public void deveChamarMetodoAtualiza() {
		Calendar antiga = Calendar.getInstance();
		antiga.set(1999, 1, 20);

        Leilao leilao1 = new CriadorDeLeilao().para("TV de plasma")
            .naData(antiga).constroi();
        
        List<Leilao> retorno = Arrays.asList(leilao1);
        
        RepositorioDeLeiloes daoFalso = mock(RepositorioDeLeiloes.class);
        EnviadorDeEmail enviadorFalso = mock(EnviadorDeEmail.class);
        when(daoFalso.correntes()).thenReturn(retorno);
        
        EncerradorDeLeilao encerrador = new EncerradorDeLeilao(daoFalso, enviadorFalso);
        encerrador.encerra();
        
        verify(daoFalso, times(1)).atualiza(leilao1);
	}
	
	@Test
    public void naoDeveEncerrarLeiloesQueComecaramMenosDeUmaSemanaAtras() {

        Calendar ontem = Calendar.getInstance();
        ontem.add(Calendar.DAY_OF_MONTH, -1);

        Leilao leilao1 = new CriadorDeLeilao().para("TV de plasma")
            .naData(ontem).constroi();
        Leilao leilao2 = new CriadorDeLeilao().para("Geladeira")
            .naData(ontem).constroi();

        RepositorioDeLeiloes daoFalso = mock(LeilaoDao.class);
        EnviadorDeEmail enviadorFalso = mock(EnviadorDeEmail.class);
        when(daoFalso.correntes()).thenReturn(Arrays.asList(leilao1, leilao2));

        EncerradorDeLeilao encerrador = new EncerradorDeLeilao(daoFalso, enviadorFalso);
        encerrador.encerra();

        assertEquals(0, encerrador.getTotalEncerrados());
        assertFalse(leilao1.isEncerrado());
        assertFalse(leilao2.isEncerrado());

        verify(daoFalso, never()).atualiza(leilao1);
        verify(daoFalso, never()).atualiza(leilao2);
    }
	
	@Test
	public void deveEnviarLeiloesPorEmail() {
		Calendar antiga = Calendar.getInstance();
		antiga.set(1999, 1, 20);

        Leilao leilao1 = new CriadorDeLeilao().para("TV de plasma")
            .naData(antiga).constroi();
        Leilao leilao2 = new CriadorDeLeilao().para("Geladeira")
            .naData(antiga).constroi();
        
        List<Leilao> retorno = Arrays.asList(leilao1, leilao2);
        
        RepositorioDeLeiloes daoFalso = mock(RepositorioDeLeiloes.class);
        EnviadorDeEmail enviadorFalso = mock(EnviadorDeEmail.class);
        when(daoFalso.correntes()).thenReturn(retorno);
        
        EncerradorDeLeilao encerrador = new EncerradorDeLeilao(daoFalso, enviadorFalso);
        encerrador.encerra();

        assertEquals(2, encerrador.getTotalEncerrados());
        assertTrue(leilao1.isEncerrado());
        assertTrue(leilao2.isEncerrado());
        
        InOrder inOrder = inOrder(daoFalso, enviadorFalso);
        // a primeira invocação
        inOrder.verify(daoFalso, times(1)).atualiza(leilao1);    
        // a segunra invocação
        inOrder.verify(enviadorFalso, times(1)).envia(leilao1);
	}
}
