package org.openpaas.servicebroker.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openpaas.servicebroker.container.platform.model.JpaServiceInstance;
import org.openpaas.servicebroker.container.platform.model.User;
import org.openpaas.servicebroker.container.platform.repo.JpaServiceInstanceRepository;
import org.openpaas.servicebroker.container.platform.service.PropertyService;
import org.openpaas.servicebroker.container.platform.service.RestTemplateService;
import org.openpaas.servicebroker.container.platform.service.impl.CatalogServiceImpl;
import org.openpaas.servicebroker.container.platform.service.impl.InstanceServiceImpl;
import org.openpaas.servicebroker.container.platform.service.impl.ContainerPlatformService;
import org.openpaas.servicebroker.container.platform.service.impl.UserService;
import org.openpaas.servicebroker.exception.ServiceBrokerException;
import org.openpaas.servicebroker.exception.ServiceInstanceExistsException;
import org.openpaas.servicebroker.model.CreateServiceInstanceRequest;
import org.openpaas.servicebroker.model.DeleteServiceInstanceRequest;
import org.openpaas.servicebroker.model.Plan;
import org.openpaas.servicebroker.model.ServiceInstance;
import org.openpaas.servicebroker.model.UpdateServiceInstanceRequest;
import org.openpaas.servicebroker.model.fixture.PlanFixture;
import org.openpaas.servicebroker.model.fixture.RequestFixture;
import org.openpaas.servicebroker.model.fixture.ServiceFixture;
import org.paasta.servicebroker.apiplatform.common.TestConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.HttpStatusCodeException;


@RunWith(MockitoJUnitRunner.class)
public class InstanceServiceImplTest {
    
    private static final Logger logger = LoggerFactory.getLogger(InstanceServiceImplTest.class);
    
    @Mock
    private CatalogServiceImpl catalog;

    @Mock
    private JpaServiceInstanceRepository instanceRepository;

    @Mock
    ContainerPlatformService caasService;
    
    @Mock
    PropertyService propertyService;
    
    @Mock
    UserService userService;
    
    @Mock
    RestTemplateService restTemplateService;
    
    @InjectMocks
    InstanceServiceImpl serviceInstance;
    
    public static JpaServiceInstance jpaServiceInstance;
    
    public static JpaServiceInstance jpaServiceInstanceDef;

    private CreateServiceInstanceRequest request;
    private static DeleteServiceInstanceRequest delRequest;
    private static UpdateServiceInstanceRequest upRequest;
    private static Plan plan;
    private static User user;
    
    @Before
    public void setUp() throws Exception {
        
        request = RequestFixture.getCreateServiceInstanceRequest();
        delRequest = RequestFixture.getDeleteServiceInstanceRequest2();
        upRequest = RequestFixture.getUpdateServiceInstanceRequest();
        
        
        jpaServiceInstance = new JpaServiceInstance(request);
        jpaServiceInstance.setServiceInstanceId(TestConstants.SV_INSTANCE_ID_001);
        jpaServiceInstance.setCaasAccountTokenName(TestConstants.JPA_CAAS_ACCOUNT_ACCESS_TOKEN);
        jpaServiceInstance.setCaasAccountName(TestConstants.JPA_CAAS_ACCOUNT_NAME);
        jpaServiceInstance.setCaasNamespace(TestConstants.JPA_CAAS_NAMESPACE);
        jpaServiceInstance.setUserId(TestConstants.PARAM_KEY_OWNER_VALUE);
        Map<String,Object> jpaMap = new HashMap<>();
        jpaMap.put(TestConstants.PARAM_KEY_OWNER, TestConstants.PARAM_KEY_OWNER_VALUE);
        jpaServiceInstance.setParameters(jpaMap);
        
        jpaServiceInstanceDef = new JpaServiceInstance(RequestFixture.getCreateServiceInstanceRequest2());
        
        plan = new Plan("test", "Micro", "Test-desc");
        
    }
    
    /**
     * ??? ???????????? ????!
     * @throws ServiceBrokerException 
     * @throws ServiceInstanceExistsException 
     * */ 
    @Test
    public void testCreateServiceInstance() throws ServiceInstanceExistsException, ServiceBrokerException {
        
        // ?????? ????????????.
        request.withServiceInstanceId(TestConstants.SV_INSTANCE_ID_001);
        
        when(instanceRepository.findByServiceInstanceId(TestConstants.SV_INSTANCE_ID_001)).thenReturn(null);
        when(caasService.existsNamespace(TestConstants.JPA_CAAS_NAMESPACE)).thenReturn(true);
        when(catalog.getServiceDefinition(jpaServiceInstance.getServiceDefinitionId())).thenReturn(ServiceFixture.getService());
        when(caasService.createNamespaceUser(jpaServiceInstance, PlanFixture.getPlanOne())).thenReturn(jpaServiceInstance);
        when(instanceRepository.save(jpaServiceInstance)).thenReturn(jpaServiceInstance);
       // doNothing().when(userService).request(jpaServiceInstance, HttpMethod.POST);
        //when(propertyService.getDashboardUrl(jpaServiceInstance.getServiceInstanceId())).thenReturn(TestConstants.DASHBOARD_URL);
        //when(inspectionProjectService.deleteProject(gTestResultJobModel)).thenThrow(Exception.class);
        
        // ?????? ????????? ????????????.
        JpaServiceInstance result = serviceInstance.createServiceInstance(request);
        
        // ?????? ???????????????.        
        
    }
    
//    /**
//     * kubernetes namespace??? ?????????, DB ????????? ????????? ??????
//     * @throws ServiceBrokerException 
//     * @throws ServiceInstanceExistsException 
//     * */ 
//    @Test(expected=HttpStatusCodeException.class)
//    public void testCreateServiceInstanceFailSave() throws ServiceInstanceExistsException, ServiceBrokerException, HttpStatusCodeException {
//        
//        // ?????? ????????????.
//        request.withServiceInstanceId(TestConstants.SV_INSTANCE_ID_001);
//        
//        when(instanceRepository.findByServiceInstanceId(TestConstants.SV_INSTANCE_ID_001)).thenReturn(null);
//        when(caasService.existsNamespace(TestConstants.JPA_CAAS_NAMESPACE)).thenReturn(true);
//        when(catalog.getServiceDefinition(jpaServiceInstance.getServiceDefinitionId())).thenReturn(ServiceFixture.getService());
//        when(caasService.createNamespaceUser(jpaServiceInstance, PlanFixture.getPlanOne())).thenReturn(jpaServiceInstance);
//        when(instanceRepository.save(jpaServiceInstance)).thenReturn(jpaServiceInstance);
//        doNothing().when(userService).request(jpaServiceInstance, plan, HttpMethod.POST);
//        when(userService.convert(jpaServiceInstance, plan)).thenReturn(user);
//        doThrow(HttpStatusCodeException.class).when(restTemplateService).requestUser(user, HttpMethod.POST);
////        doThrow(HttpStatusCodeException.class).when(userService).request(jpaServiceInstance, plan, HttpMethod.POST); //HttpStatusCodeException
//        //userService.request(instance, getPlan(instance), HttpMethod.POST);
//        // ?????? ????????? ????????????.
//        JpaServiceInstance result = serviceInstance.createServiceInstance(request);
//        
//        // ?????? ???????????????.        
//        
//    }
    
    /**
     * kubernetes namespace??? ?????????, DB ???????????? ??? common DB??? ?????? ????????? ??????
     * @throws ServiceBrokerException 
     * @throws ServiceInstanceExistsException 
     * */ 
    @Test
    public void testCreateServiceInstanceFailSend() throws ServiceInstanceExistsException, ServiceBrokerException {
        
        // ?????? ????????????.
        request.withServiceInstanceId(TestConstants.SV_INSTANCE_ID_001);
        
        when(instanceRepository.findByServiceInstanceId(TestConstants.SV_INSTANCE_ID_001)).thenReturn(null);
        when(caasService.existsNamespace(TestConstants.JPA_CAAS_NAMESPACE)).thenReturn(true);
        when(catalog.getServiceDefinition(jpaServiceInstance.getServiceDefinitionId())).thenReturn(ServiceFixture.getService());
        when(caasService.createNamespaceUser(jpaServiceInstance, PlanFixture.getPlanOne())).thenReturn(jpaServiceInstance);
        when(instanceRepository.save(jpaServiceInstance)).thenReturn(jpaServiceInstance);
    //    doNothing().when(userService).request(jpaServiceInstance, HttpMethod.POST);
        //when(inspectionProjectService.deleteProject(gTestResultJobModel)).thenThrow(Exception.class);
        
        // ?????? ????????? ????????????.
        JpaServiceInstance result = serviceInstance.createServiceInstance(request);
        
        // ?????? ???????????????.        
        
    }
    
    
    
    /**
     * instanceService???
     * if ( findInstance == null ) ?????????
     * namespace??? ????????? ????????? ???  ??????
     * @throws ServiceBrokerException 
     * @throws ServiceInstanceExistsException 
     * */ 
    @Test(expected=ServiceBrokerException.class)
    public void testCreateServiceInstanceFindInstanceNullExistNamespace() throws ServiceInstanceExistsException, ServiceBrokerException {
        
        // ?????? ????????????.
        request.withServiceInstanceId(TestConstants.SV_INSTANCE_ID_001);
        
        when(instanceRepository.findByServiceInstanceId(TestConstants.SV_INSTANCE_ID_001)).thenReturn(null);
        when(caasService.existsNamespace(TestConstants.SV_INSTANCE_ID_001)).thenReturn(true);
        
        // ?????? ????????? ????????????.
        serviceInstance.createServiceInstance(request);
        
        // ????????? ????????? ????????????.!        
        
    }
    
    /**
     * instanceService???
     * if ( findInstance == null ) ?????????
     * org guid??? ?????? ?????? 
     * @throws ServiceBrokerException 
     * @throws ServiceInstanceExistsException 
     * */ 
    @Test(expected=ServiceBrokerException.class)
    public void testCreateServiceInstanceFindInstanceNullExistOrg() throws ServiceInstanceExistsException, ServiceBrokerException {
        
        // ?????? ????????????.
        request.withServiceInstanceId(TestConstants.SV_INSTANCE_ID_001);
        
        when(instanceRepository.findByServiceInstanceId(TestConstants.SV_INSTANCE_ID_001)).thenReturn(null);
        when(instanceRepository.existsByOrganizationGuid(TestConstants.ORG_GUID_001)).thenReturn(true);
        
        // ?????? ????????? ????????????.
        serviceInstance.createServiceInstance(request);
        
        // ????????? ????????? ????????????.!        
        
    }
    
    /**
     * instanceService???
     * if ( findInstance != null ) ??? true??? ???
     * findInstance ??? instance ?????? ?????? ?????? ???
     * @throws ServiceBrokerException 
     * @throws ServiceInstanceExistsException 
     * */ 
    @Test(expected=ServiceBrokerException.class)
    public void testCreateServiceInstanceFindInstanceNotNull() throws ServiceInstanceExistsException, ServiceBrokerException {
        
        // ?????? ????????????.
        request.withServiceInstanceId(TestConstants.SV_INSTANCE_ID_001);
        
        when(instanceRepository.findByServiceInstanceId(TestConstants.SV_INSTANCE_ID_001)).thenReturn(jpaServiceInstance);
        
        // ?????? ????????? ????????????.
        serviceInstance.createServiceInstance(request);
        
        // ?????? ????????????.        
        
    }
    
    /**
     * instanceService???
     * if ( findInstance != null ) ??? true??? ???
     * findInstance ??? instance ?????? ?????? ??? ??????
     * @throws ServiceBrokerException 
     * @throws ServiceInstanceExistsException 
     * */ 
    @Test(expected=ServiceInstanceExistsException.class)
    public void testCreateServiceInstanceFindInstanceNotNullDef() throws ServiceInstanceExistsException, ServiceBrokerException {
        
        // ?????? ????????????.
        request.withServiceInstanceId(TestConstants.SV_INSTANCE_ID_001);
        jpaServiceInstanceDef.setServiceInstanceId(TestConstants.SV_INSTANCE_ID_002);
        
        when(instanceRepository.findByServiceInstanceId(TestConstants.SV_INSTANCE_ID_001)).thenReturn(jpaServiceInstanceDef);
                
        
        // ?????? ????????? ????????????.
        serviceInstance.createServiceInstance(request);
        
        // ?????? ????????????.        
        
    }

    
    // TODO : ??????
    @Test
    public void testGetServiceInstance() {
        // ?????? ????????????.
        when(instanceRepository.findByServiceInstanceId(request.getServiceInstanceId())).thenReturn(jpaServiceInstance);
        // ????????? ???????????? ????????? ????????????.
        ServiceInstance instance = serviceInstance.getServiceInstance(request.getServiceInstanceId());
        //?????? ?????? ????????? ??????.
        assertThat(instance).isNotNull();
        assertEquals(jpaServiceInstance, instance);
        assertEquals(TestConstants.JPA_CAAS_ACCOUNT_ACCESS_TOKEN, jpaServiceInstance.getCaasAccountTokenName());
        assertEquals(TestConstants.JPA_CAAS_ACCOUNT_NAME, jpaServiceInstance.getCaasAccountName());
        assertEquals(TestConstants.JPA_CAAS_NAMESPACE, jpaServiceInstance.getCaasNamespace());
        assertEquals(TestConstants.JPA_ORGANIZTION_GUID, jpaServiceInstance.getOrganizationGuid());
        assertEquals(TestConstants.JPA_SERVICE_DEFINITION_ID, jpaServiceInstance.getServiceDefinitionId());
        assertEquals(TestConstants.JPA_SPACE_GUID, jpaServiceInstance.getSpaceGuid());
        assertEquals(TestConstants.PARAM_KEY_OWNER_VALUE, jpaServiceInstance.getParameter(TestConstants.PARAM_KEY_OWNER));
        assertEquals(TestConstants.PARAM_KEY_OWNER_VALUE, jpaServiceInstance.getUserId());
        
    }
    
    /** ??????
     * instanceService???
     * if ( instance == null ) ??? true??? ???
     * */ 
    @Test
    public void testDeleteServiceInstanceNull() throws ServiceBrokerException {
        //?????? ????????????.
        when(instanceRepository.findByServiceInstanceId(request.getServiceInstanceId())).thenReturn(null);

        //????????? ???????????? ????????? ????????????.
        ServiceInstance instance = serviceInstance.deleteServiceInstance(delRequest);    
        assertNull(instance);
        
    }
    
    /** 
     * instanceService???
     * if ( instance == null ) ??? true??????, 
     * if ( existsNamespace ) ??? false ??? ???
     * */ 
    @Test
    public void testDeleteServiceInstanceNullExistFalse() throws ServiceBrokerException {
        //?????? ????????????.
        when(instanceRepository.findByServiceInstanceId(request.getServiceInstanceId())).thenReturn(null);

        when(caasService.existsNamespace(TestConstants.JPA_CAAS_NAMESPACE)).thenReturn(false);
        
        //????????? ???????????? ????????? ????????????.
        ServiceInstance instance = serviceInstance.deleteServiceInstance(delRequest);    
        assertNull(instance);
        
    }
    
    /**
     * instanceService???
     * if ( instance == null ) ??? true??????, 
     * if ( existsNamespace ) ??? true ??? ???
     * */ 
    @Test
    public void testDeleteServiceInstanceNullExistTrue() throws ServiceBrokerException {
        //?????? ????????????.
        when(instanceRepository.findByServiceInstanceId(request.getServiceInstanceId())).thenReturn(null);
        
        when(caasService.existsNamespace(TestConstants.JPA_CAAS_NAMESPACE)).thenReturn(true);
        doNothing().when(caasService).deleteNamespace(TestConstants.JPA_CAAS_NAMESPACE);

        //????????? ???????????? ????????? ????????????.
        ServiceInstance instance = serviceInstance.deleteServiceInstance(delRequest);    
        assertNull(instance);
        
    }
    
    /** ??????
     * instanceService???
     * if ( instance == null )??? ??? false ??????
     * if (existsNamespace( instance.getContainerPlatformNamespace() ))??? existsNamespace ??? return???
     * caasService.existsNamespace( namespace ) ??? true??? ??? 
     * @throws ServiceBrokerException 
     * */ 
    @Test
    public void testDeleteServiceNamespaceNotNull() throws ServiceBrokerException {
        
        // ?????? ????????????.
        request.setServiceDefinitionId(jpaServiceInstance.getServiceDefinitionId());
        when(instanceRepository.findByServiceInstanceId(TestConstants.SV_INSTANCE_ID_001)).thenReturn(jpaServiceInstance);
        when(caasService.existsNamespace(TestConstants.JPA_CAAS_NAMESPACE)).thenReturn(true);
        
        doNothing().when(caasService).deleteNamespace(TestConstants.JPA_CAAS_NAMESPACE);
        doNothing().when(instanceRepository).delete(jpaServiceInstance);
        
        
        // ????????? ????????????.
        serviceInstance.deleteServiceInstance(delRequest);    
        
        assertThat(jpaServiceInstance).isNotNull();
        assertEquals(TestConstants.JPA_CAAS_ACCOUNT_ACCESS_TOKEN, jpaServiceInstance.getCaasAccountTokenName());
        assertEquals(TestConstants.JPA_CAAS_ACCOUNT_NAME, jpaServiceInstance.getCaasAccountName());
        assertEquals(TestConstants.JPA_CAAS_NAMESPACE, jpaServiceInstance.getCaasNamespace());
        assertEquals(TestConstants.JPA_ORGANIZTION_GUID, jpaServiceInstance.getOrganizationGuid());
        assertEquals(TestConstants.JPA_SERVICE_DEFINITION_ID, jpaServiceInstance.getServiceDefinitionId());
        assertEquals(TestConstants.JPA_SPACE_GUID, jpaServiceInstance.getSpaceGuid());
        assertEquals(TestConstants.PARAM_KEY_OWNER_VALUE, jpaServiceInstance.getParameter(TestConstants.PARAM_KEY_OWNER));
    }
    
    @Test
    public void testUpdateServiceInstance() throws Exception {

        //?????? ????????????.
        request.setPlanId(jpaServiceInstance.getPlanId());
        request.setServiceDefinitionId(jpaServiceInstance.getServiceDefinitionId());

        when(instanceRepository.findByServiceInstanceId(upRequest.getServiceInstanceId())).thenReturn(jpaServiceInstance);
        when(catalog.getServiceDefinition(jpaServiceInstance.getServiceDefinitionId())).thenReturn(ServiceFixture.getService());
        

        //?????? ?????? ??????
        serviceInstance.updateServiceInstance(upRequest);
        
    }
     // ?????? ID??? ?????? ?????? ???????????? ????????????.
    @Test(expected=ServiceBrokerException.class)
    public void testUpdateServiceInstanceNoPlanId() throws Exception {

        //?????? ????????????.
        request.setPlanId(jpaServiceInstance.getPlanId());
        request.setServiceDefinitionId(jpaServiceInstance.getServiceDefinitionId());
        upRequest.setPlanId("????????????");
        
        when(instanceRepository.findByServiceInstanceId(upRequest.getServiceInstanceId())).thenReturn(jpaServiceInstance);
        when(catalog.getServiceDefinition(jpaServiceInstance.getServiceDefinitionId())).thenReturn(ServiceFixture.getService());
        

        //?????? ?????? ??????
        serviceInstance.updateServiceInstance(upRequest);
        
        // ????????? ???????????? ?????? ???????
        
        
    }
    
    // findeInstance ??? instance ??? ?????? ??? ??????
    @Test
    public void testUpdateServiceInstanceNotEqual() throws Exception {

        //?????? ????????????.
        request.setPlanId(jpaServiceInstance.getPlanId());
        request.setServiceDefinitionId(jpaServiceInstance.getServiceDefinitionId());
        jpaServiceInstanceDef.setServiceInstanceId(TestConstants.SV_INSTANCE_ID_002);
        
        when(instanceRepository.findByServiceInstanceId(upRequest.getServiceInstanceId())).thenReturn(jpaServiceInstanceDef);
        when(catalog.getServiceDefinition(jpaServiceInstance.getServiceDefinitionId())).thenReturn(ServiceFixture.getService());
    
        
        //?????? ?????? ??????
        serviceInstance.updateServiceInstance(upRequest);
    
        // ??? ?????? find??? ?????? ???????????? ???????????? ?????? ????????????.
    
        
    }
    
    // plan Id ??? ?????? ???  ??????
    @Test
    public void testUpdateServiceInstancePlanIdNull() throws Exception {

        //?????? ????????????.
        upRequest.setPlanId(null);
        upRequest.setServiceDefinitionId(jpaServiceInstance.getServiceDefinitionId());
        
        when(instanceRepository.findByServiceInstanceId(upRequest.getServiceInstanceId())).thenReturn(jpaServiceInstance);
        when(catalog.getServiceDefinition(jpaServiceInstance.getServiceDefinitionId())).thenReturn(ServiceFixture.getService());
        

        //?????? ?????? ??????
        serviceInstance.updateServiceInstance(upRequest);
        
        // ??? ?????? ?????? ????????????
        
    }
    
    // findInstace??? ?????????  ?????? 
    @Test(expected=ServiceBrokerException.class)
    public void testUpdateServiceInstanceNull() throws ServiceBrokerException {
        when(instanceRepository.findByServiceInstanceId(upRequest.getServiceInstanceId())).thenReturn(null);
        
        //?????? ?????? ??????
        serviceInstance.updateServiceInstance(upRequest);
        
        // TODO ????????? ????????? ???! ???????????? ???????????? ?????? ??????
    }
    

}

//        //PowerMockito.whenNew(JpaServiceInstance.class).withArguments(upRequest).thenReturn(jpaServiceInstance);
