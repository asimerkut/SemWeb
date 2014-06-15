package org.abacus.organization.core.persistance;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.abacus.common.shared.AbcBusinessException;
import org.abacus.definition.shared.constant.EnumList;
import org.abacus.organization.core.persistance.repository.FiscalPeriodRepository;
import org.abacus.organization.core.persistance.repository.FiscalYearRepository;
import org.abacus.organization.shared.FiscalPeriodNotFoundException;
import org.abacus.organization.shared.FiscalPeriodNotOpenAccException;
import org.abacus.organization.shared.FiscalPeriodNotOpenFinException;
import org.abacus.organization.shared.FiscalPeriodNotOpenStkException;
import org.abacus.organization.shared.FiscalYearDocumentDateNotMatchedException;
import org.abacus.organization.shared.entity.FiscalPeriodEntity;
import org.abacus.organization.shared.entity.FiscalYearEntity;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@SuppressWarnings("serial")
@Service
public class FiscalDao implements Serializable {

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private FiscalYearRepository fiscalYearRepository;

	@Autowired
	private FiscalPeriodRepository fiscalPeriodRepository;

	public FiscalYearEntity findFiscalYear(String id) throws AbcBusinessException {
		return fiscalYearRepository.findOne(id);
	}

		
	public FiscalPeriodEntity findFiscalPeriod(FiscalYearEntity fiscalYearEntity, Date docDate, EnumList.DefTypeEnum docTypeEnum) throws AbcBusinessException {
		if (!String.valueOf(new DateTime(docDate).getYear()).equals(fiscalYearEntity.getYear())){
			throw new FiscalYearDocumentDateNotMatchedException();
		}
		FiscalPeriodEntity fiscalPeriod = fiscalPeriodRepository.findFiscalPeriod(fiscalYearEntity.getId(), docDate);
		if (fiscalPeriod==null){
			throw new FiscalPeriodNotFoundException();
		}
		if (docTypeEnum.name().startsWith(EnumList.DefTypeGroupEnum.ACC.name()) && !fiscalPeriod.getIsAccActive()){
			throw new FiscalPeriodNotOpenAccException();
		}
		if (docTypeEnum.name().startsWith(EnumList.DefTypeGroupEnum.FIN.name()) && !fiscalPeriod.getIsFinActive()){
			throw new FiscalPeriodNotOpenFinException();
		}
		if (docTypeEnum.name().startsWith(EnumList.DefTypeGroupEnum.STK.name()) && !fiscalPeriod.getIsStkActive()){
			throw new FiscalPeriodNotOpenStkException();
		}
		return fiscalPeriod;
	}	

}
