package org.abacus.transaction.web;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.abacus.common.shared.AbcBusinessException;
import org.abacus.common.web.JsfDialogHelper;
import org.abacus.common.web.JsfMessageHelper;
import org.abacus.common.web.SessionInfoHelper;
import org.abacus.definition.core.persistance.repository.DefTaskRepository;
import org.abacus.definition.shared.constant.EnumList;
import org.abacus.definition.shared.entity.DefItemEntity;
import org.abacus.definition.shared.entity.DefTaskEntity;
import org.abacus.transaction.core.handler.ReqConfirmationHandler;
import org.abacus.transaction.core.handler.ReqOfferHandler;
import org.abacus.transaction.core.handler.TraTransactionHandler;
import org.abacus.transaction.shared.entity.ReqDetailEntity;
import org.abacus.transaction.shared.entity.ReqDetailOfferEntity;
import org.abacus.transaction.shared.entity.ReqDocumentEntity;
import org.abacus.transaction.shared.event.ConfirmDocumentEvent;
import org.abacus.transaction.shared.event.CreateDetailEvent;
import org.abacus.transaction.shared.event.CreateDocumentEvent;
import org.abacus.transaction.shared.event.DeleteDetailEvent;
import org.abacus.transaction.shared.event.DetailCreatedEvent;
import org.abacus.transaction.shared.event.DetailDeletedEvent;
import org.abacus.transaction.shared.event.DetailUpdatedEvent;
import org.abacus.transaction.shared.event.DocumentCreatedEvent;
import org.abacus.transaction.shared.event.DocumentUpdatedEvent;
import org.abacus.transaction.shared.event.ReadDetailEvent;
import org.abacus.transaction.shared.event.ReadDocumentEvent;
import org.abacus.transaction.shared.event.RequestReadDetailEvent;
import org.abacus.transaction.shared.event.RequestReadDocumentEvent;
import org.abacus.transaction.shared.event.UpdateDetailEvent;
import org.abacus.transaction.shared.event.UpdateDocumentEvent;
import org.abacus.transaction.shared.holder.TraDocumentSearchCriteria;
import org.abacus.user.core.persistance.repository.UserOrganizationRepository;
import org.abacus.user.shared.entity.SecUserOrganizationEntity;
import org.springframework.util.CollectionUtils;

@SuppressWarnings("serial")
@ManagedBean
@ViewScoped
public class CrudPurchaseDocumentViewBean implements Serializable {

	@ManagedProperty(value = "#{jsfMessageHelper}")
	private JsfMessageHelper jsfMessageHelper;

	@ManagedProperty(value = "#{sessionInfoHelper}")
	private SessionInfoHelper sessionInfoHelper;

	@ManagedProperty(value = "#{jsfDialogHelper}")
	private JsfDialogHelper jsfDialogHelper;

	@ManagedProperty(value = "#{reqTransactionHandler}")
	private TraTransactionHandler<ReqDocumentEntity, ReqDetailEntity> transactionHandler;

	@ManagedProperty(value = "#{defTaskRepository}")
	private DefTaskRepository taskRepository;

	@ManagedProperty(value = "#{reqConfirmationHandler}")
	private ReqConfirmationHandler reqConfirmationHandler;

	@ManagedProperty(value = "#{userOrganizationRepository}")
	private UserOrganizationRepository userOrgRepo;
	
	@ManagedProperty(value = "#{reqOfferHandler}")
	private ReqOfferHandler reqOfferHandler;

	private ReqDocumentEntity document;

	private List<ReqDetailEntity> detailList;

	private DefItemEntity vendor;
	
	private ReqDetailEntity selectedDetail;
	
	private ReqDetailOfferEntity selectedOffer;

	@PostConstruct
	private void init() {

		String documentId = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("document");

		String organizationId = sessionInfoHelper.currentOrganizationId();
		String username = sessionInfoHelper.currentUserName();
		this.vendor = userOrgRepo.findVendorByUserAndOrganization(username, organizationId);

		this.findDocument(Long.valueOf(documentId));

	}

	public void offerDetailSelected() {

	}

	private void findDocument(Long documentId) {
		TraDocumentSearchCriteria traDocumentSearchCriteria = new TraDocumentSearchCriteria(documentId);

		DefTaskEntity purchaseTaskList = taskRepository.getTaskList(sessionInfoHelper.currentRootOrganizationId(), EnumList.DefTypeEnum.REQ_IO_P.name()).get(0);
		traDocumentSearchCriteria.setDocTask(purchaseTaskList);

		ReadDocumentEvent<ReqDocumentEntity> readDocumentEvent = transactionHandler.readDocumentList(new RequestReadDocumentEvent<ReqDocumentEntity>(traDocumentSearchCriteria, sessionInfoHelper.currentOrganizationId(), sessionInfoHelper.selectedFiscalYearId()));
		if (CollectionUtils.isEmpty(readDocumentEvent.getDocumentList())) {
			document = null;
		} else {
			document = readDocumentEvent.getDocumentList().get(0);
			ReadDetailEvent<ReqDetailEntity> readDetailEvent = transactionHandler.readDetailList(new RequestReadDetailEvent<ReqDetailEntity>(document.getId()));
			detailList = readDetailEvent.getDetails();
		}
	}
	
	public ReqDetailOfferEntity vendorOffer(ReqDetailEntity detail){
		Set<ReqDetailOfferEntity> offerSet = detail.getOfferSet();
		
		ReqDetailOfferEntity offer = null;
		if(!CollectionUtils.isEmpty(offerSet)){
			for(ReqDetailOfferEntity coffer : offerSet){
				if(coffer.getVendorItem().getId().equals(vendor.getId())){
					offer = coffer;
				}
			}
		}
		
		if(offer == null){
			offer = this.initNewOffer(detail);
		}
		
		return offer;
		
	}
	
	private ReqDetailOfferEntity initNewOffer(ReqDetailEntity detail){
		ReqDetailOfferEntity offer = new ReqDetailOfferEntity();
		offer.setDetail(detail);
		offer.setVendorItem(vendor);
		return offer;
	}
	
	public void offerSelected(ReqDetailEntity detail, ReqDetailOfferEntity offer){
		this.selectedDetail = detail;
		this.selectedOffer = offer;
	}
	
//	public void deleteOffer(ReqDetailOfferEntity offer){
//		reqOfferHandler.deleteOffer(offer);
//		this.findDocument(document.getId());
//	}
//	
//	public void saveOffer(){
//		OfferCreatedEvent createdEvent = reqOfferHandler.saveOffer(new CreateOfferEvent());
//		this.findDocument(document.getId());
//	}
//	
//	public void updateOffer(){
//		OfferUpdatedEvent updatedEvent = reqOfferHandler.updateOffer(new CreateOfferEvent());
//		this.findDocument(document.getId());
//	}

	public JsfMessageHelper getJsfMessageHelper() {
		return jsfMessageHelper;
	}

	public void setJsfMessageHelper(JsfMessageHelper jsfMessageHelper) {
		this.jsfMessageHelper = jsfMessageHelper;
	}

	public SessionInfoHelper getSessionInfoHelper() {
		return sessionInfoHelper;
	}

	public void setSessionInfoHelper(SessionInfoHelper sessionInfoHelper) {
		this.sessionInfoHelper = sessionInfoHelper;
	}

	public JsfDialogHelper getJsfDialogHelper() {
		return jsfDialogHelper;
	}

	public void setJsfDialogHelper(JsfDialogHelper jsfDialogHelper) {
		this.jsfDialogHelper = jsfDialogHelper;
	}

	public TraTransactionHandler<ReqDocumentEntity, ReqDetailEntity> getTransactionHandler() {
		return transactionHandler;
	}

	public void setTransactionHandler(TraTransactionHandler<ReqDocumentEntity, ReqDetailEntity> transactionHandler) {
		this.transactionHandler = transactionHandler;
	}

	public DefTaskRepository getTaskRepository() {
		return taskRepository;
	}

	public void setTaskRepository(DefTaskRepository taskRepository) {
		this.taskRepository = taskRepository;
	}

	public ReqConfirmationHandler getReqConfirmationHandler() {
		return reqConfirmationHandler;
	}

	public void setReqConfirmationHandler(ReqConfirmationHandler reqConfirmationHandler) {
		this.reqConfirmationHandler = reqConfirmationHandler;
	}

	public UserOrganizationRepository getUserOrgRepo() {
		return userOrgRepo;
	}

	public void setUserOrgRepo(UserOrganizationRepository userOrgRepo) {
		this.userOrgRepo = userOrgRepo;
	}

	public ReqDocumentEntity getDocument() {
		return document;
	}

	public void setDocument(ReqDocumentEntity document) {
		this.document = document;
	}

	public List<ReqDetailEntity> getDetailList() {
		return detailList;
	}

	public void setDetailList(List<ReqDetailEntity> detailList) {
		this.detailList = detailList;
	}

	public DefItemEntity getVendor() {
		return vendor;
	}

	public void setVendor(DefItemEntity vendor) {
		this.vendor = vendor;
	}

	public ReqOfferHandler getReqOfferHandler() {
		return reqOfferHandler;
	}

	public void setReqOfferHandler(ReqOfferHandler reqOfferHandler) {
		this.reqOfferHandler = reqOfferHandler;
	}

	public ReqDetailEntity getSelectedDetail() {
		return selectedDetail;
	}

	public void setSelectedDetail(ReqDetailEntity selectedDetail) {
		this.selectedDetail = selectedDetail;
	}

	public ReqDetailOfferEntity getSelectedOffer() {
		return selectedOffer;
	}

	public void setSelectedOffer(ReqDetailOfferEntity selectedOffer) {
		this.selectedOffer = selectedOffer;
	}
	
	

}
