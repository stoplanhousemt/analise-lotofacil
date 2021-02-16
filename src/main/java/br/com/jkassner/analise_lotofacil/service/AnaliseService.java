package br.com.jkassner.analise_lotofacil.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import br.com.jkassner.analise_lotofacil.model.ConcursoLotoFacil;
import br.com.jkassner.analise_lotofacil.model.DezenasLotoFacilOrdenadas;
import br.com.jkassner.analise_lotofacil.repository.ConcursoLotoFacilRepository;
import br.com.jkassner.analise_lotofacil.response.AnaliseResponse;

@Service
public class AnaliseService {

	@Autowired
	ConcursoLotoFacilRepository concursoLotoFacilRepository;
	
	public AnaliseResponse analisar(int qtJogos) {
		PageRequest page = PageRequest.of(0, qtJogos, Direction.DESC, "id");
		List<ConcursoLotoFacil> concursos = concursoLotoFacilRepository.getUltimosConcursos(page);
		
		List<Integer> dezenasList = new ArrayList<>();
		concursos.forEach(c -> {
			dezenasList.add(c.getPrDezena());	
			dezenasList.add(c.getSeDezena());	
			dezenasList.add(c.getTeDezena());	
			dezenasList.add(c.getQaDezena());	
			dezenasList.add(c.getQiDezena());	
			dezenasList.add(c.getSxDezena());	
			dezenasList.add(c.getStDezena());	
			dezenasList.add(c.getOtDezena());	
			dezenasList.add(c.getNoDezena());	
			dezenasList.add(c.getDcDezena());	
			dezenasList.add(c.getDprDezena());	
			dezenasList.add(c.getDseDezena());	
			dezenasList.add(c.getDteDezena());	
			dezenasList.add(c.getDqaDezena());	
			dezenasList.add(c.getDqiDezena());	
		});
		
		Map<Integer, List<Integer>> collect = dezenasList.stream().collect(Collectors.groupingBy(Function.identity()));
		
		Map<String, Integer> sequencies = mountSequencies(concursos);
		
		AnaliseResponse analiseResponse = new AnaliseResponse();
		analiseResponse.setCounterDezenas(collect);
		analiseResponse.setConcursos(concursos);
		analiseResponse.setSequencias(sequencies);
		
		return analiseResponse;
	}
	
	private Map<String, Integer> mountSequencies(List<ConcursoLotoFacil> concursos) {
		
		Map<String, Integer> sequencies = new HashMap<>();
		
		concursos.forEach(concurso -> {
			
			Iterator<Integer> iterator = createIteratorWithDezenas(concurso.getDezenasLotoFacilOrdenadas());
			
			Integer atual = (Integer) iterator.next();
			String sequencieAtual = "";
			
			while (iterator.hasNext()) {
				
				Integer proxima = (Integer) iterator.next();
				
				String sequencie = getSequencie(sequencieAtual, atual, proxima);
				
				if (!ObjectUtils.isEmpty(sequencie)) {

					if (sequencie.equals(sequencieAtual)) {
						
						if (sequencies.containsKey(sequencie)) {
							sequencies.put(sequencie, sequencies.get(sequencie) + 1);
						} else {
							sequencies.put(sequencie, 1);
						}

						sequencieAtual = "";
						
					} else {
						sequencieAtual = sequencie;
					}
				}
								
				atual = proxima;
			}
		});
		
		return sequencies;
	}
	
	private Iterator<Integer> createIteratorWithDezenas(DezenasLotoFacilOrdenadas concurso) {
		
		return Arrays.asList(
				concurso.getPrimeira(),
				concurso.getSegunda(),
				concurso.getTerceira(),
				concurso.getQuarta(),
				concurso.getQuinta(),
				concurso.getSexta(),
				concurso.getSetima(),
				concurso.getOitava(),
				concurso.getNona(),
				concurso.getDecima(),
				concurso.getDecimaPrimeira(),
				concurso.getDecimaSegunda(),
				concurso.getDecimaTerceira(),
				concurso.getDecimaQuarta(),
				concurso.getDecimaQuinta()
				).iterator();
	}
	
	private String getSequencie(String sequencieAtual, Integer dezenaAtual, Integer proximaDezena) {
		
		if(new Integer(dezenaAtual + 1).equals(proximaDezena)) {
			
			if(ObjectUtils.isEmpty(sequencieAtual)) {
				
				return String.valueOf(dezenaAtual).concat(";").concat(String.valueOf(proximaDezena));
				
			} else {
				
				return sequencieAtual.concat(";").concat(String.valueOf(proximaDezena));
			}
		}
		
		return sequencieAtual;
	}
}
