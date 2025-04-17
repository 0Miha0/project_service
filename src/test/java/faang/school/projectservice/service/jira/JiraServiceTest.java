package faang.school.projectservice.service.jira;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.config.webclient.jira.JiraClientConfig;
import faang.school.projectservice.dto.jira.issue.*;
import faang.school.projectservice.dto.jira.issue.filter.JiraIssueFilterDto;
import faang.school.projectservice.dto.jira.issue_link.JiraIssueLinkCreateDto;
import faang.school.projectservice.dto.jira.issue_transition.JiraIssueAvailableTransitionsDto;
import faang.school.projectservice.dto.jira.issue_transition.JiraIssueTransitionSetDto;
import faang.school.projectservice.dto.user_jira.UserJiraDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.webclient.WebClientErrorHandler;
import faang.school.projectservice.filter.jira.issue.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JiraServiceTest {

    private static final String SEARCH_ENDPOINT = "/search";

    @Mock
    private UserContext userContext;

    @Mock
    private JiraClientConfig jiraClientConfig;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private WebClientErrorHandler webClientErrorHandler;

    @Captor
    ArgumentCaptor<String> uriCaptor;

    private JiraService jiraService;

    @BeforeEach
    void setUp() {
        List<JiraIssueFilter> filters = List.of(
                new JiraIssueAssigneeFilter(),
                new JiraIssueProjectFilter(),
                new JiraIssueStatusFilter()
        );
        jiraService = new JiraService(userContext, jiraClientConfig, userServiceClient, webClientErrorHandler, filters);
        ReflectionTestUtils.setField(jiraService, "issueEndpoint", "/issue");
        ReflectionTestUtils.setField(jiraService, "issueLinkEndpoint", "/issueLink");
        ReflectionTestUtils.setField(jiraService, "searchEndpoint", "/search");
    }

    @Test
    void createIssueSubtaskWithoutParentTest() {
        String jiraDomain = "mad1x";
        JiraIssueCreateDto createDto = new JiraIssueCreateDto();
        JiraIssueBaseDto.Fields.IssueType issueType = new JiraIssueBaseDto.Fields.IssueType();
        issueType.setName("Subtask");
        createDto.setFields(new JiraIssueCreateDto.Fields());
        createDto.getFields().setIssuetype(issueType);

        assertThrows(DataValidationException.class, () -> jiraService.createIssue(jiraDomain, createDto));

        verify(userContext, times(1)).getUserId();
    }

    @Test
    void createIssueValidTest() {
        Class<JiraIssueCreateResponseDto> responseType = JiraIssueCreateResponseDto.class;
        JiraIssueCreateResponseDto response = new JiraIssueCreateResponseDto();
        HttpMethod method = HttpMethod.POST;

        long userId = 2L;
        String userJiraEmail = "example@gmail.com";
        String userJiraToken = "901284v09182489124m9812984";
        String jiraDomain = "mad1x";

        String issueTypeName = "Subtask";
        JiraIssueCreateDto createDto = new JiraIssueCreateDto();
        JiraIssueBaseDto.Fields.Parent issueParent = new JiraIssueBaseDto.Fields.Parent();
        JiraIssueBaseDto.Fields.IssueType issueType = new JiraIssueBaseDto.Fields.IssueType();
        issueType.setName(issueTypeName);
        createDto.setFields(new JiraIssueCreateDto.Fields());
        createDto.getFields().setIssuetype(issueType);
        createDto.getFields().setParent(issueParent);

        UserJiraDto userJiraInfo = new UserJiraDto();
        userJiraInfo.setJiraEmail(userJiraEmail);
        userJiraInfo.setJiraToken(userJiraToken);

        prepareMockActions(jiraDomain, method, responseType, response);

        assertDoesNotThrow(() -> jiraService.createIssue(jiraDomain, createDto));

        verify(userContext, times(2)).getUserId();
        verify(userServiceClient, times(1)).getUserJiraInfo(userId, jiraDomain);
        verify(jiraClientConfig, times(1)).getJiraClient(jiraDomain, userJiraEmail, userJiraToken);
        verify(webClientErrorHandler, times(1)).handleResponse(any(ClientResponse.class), eq(responseType));
    }

    @Test
    void updateIssueSubtaskWithoutParentTest() {
        String jiraDomain = "mad1x";
        String issueKey = "TEST-1";
        JiraIssueUpdateDto updateDto = new JiraIssueUpdateDto();
        JiraIssueBaseDto.Fields.IssueType issueType = new JiraIssueBaseDto.Fields.IssueType();
        issueType.setName("Subtask");
        updateDto.setFields(new JiraIssueCreateDto.Fields());
        updateDto.getFields().setIssuetype(issueType);

        assertThrows(DataValidationException.class, () -> jiraService.updateIssue(jiraDomain, issueKey, updateDto));

        verify(userContext, times(1)).getUserId();
    }

    @Test
    void updateIssueValidTest() {
        Class<Void> responseType = Void.class;
        HttpMethod method = HttpMethod.PUT;

        long userId = 2L;
        String userJiraEmail = "example@gmail.com";
        String userJiraToken = "901284v09182489124m9812984";
        String jiraDomain = "mad1x";
        String issueKey = "TEST-10";
        String issueTypeName = "Subtask";

        JiraIssueUpdateDto updateDto = new JiraIssueUpdateDto();
        JiraIssueBaseDto.Fields.Parent issueParent = new JiraIssueBaseDto.Fields.Parent();
        JiraIssueBaseDto.Fields.IssueType issueType = new JiraIssueBaseDto.Fields.IssueType();
        issueType.setName(issueTypeName);
        updateDto.setFields(new JiraIssueCreateDto.Fields());
        updateDto.getFields().setIssuetype(issueType);
        updateDto.getFields().setParent(issueParent);

        prepareMockActions(jiraDomain, method, responseType, null);

        assertDoesNotThrow(() -> jiraService.updateIssue(jiraDomain, issueKey, updateDto));

        verify(userContext, times(2)).getUserId();
        verify(userServiceClient, times(1)).getUserJiraInfo(userId, jiraDomain);
        verify(jiraClientConfig, times(1)).getJiraClient(jiraDomain, userJiraEmail, userJiraToken);
        verify(webClientErrorHandler, times(1)).handleResponse(any(ClientResponse.class), eq(responseType));
    }

    @Test
    void getTransitionsForIssueTest() {
        Class<JiraIssueAvailableTransitionsDto> responseType = JiraIssueAvailableTransitionsDto.class;
        JiraIssueAvailableTransitionsDto response = new JiraIssueAvailableTransitionsDto();
        HttpMethod method = HttpMethod.GET;
        String jiraDomain = "mad1x";
        String issueKey = "TEST-10";

        prepareMockActions(jiraDomain, method, responseType, response);

        assertDoesNotThrow(() -> jiraService.getTransitionsForIssue(jiraDomain, issueKey));

        verify(userContext, times(2)).getUserId();
        verify(userServiceClient, times(1)).getUserJiraInfo(anyLong(), anyString());
        verify(jiraClientConfig, times(1)).getJiraClient(anyString(), anyString(), anyString());
        verify(webClientErrorHandler, times(1)).handleResponse(any(ClientResponse.class), eq(responseType));
    }

    @Test
    void setStatusForIssueTest() {
        Class<Void> responseType = Void.class;
        HttpMethod method = HttpMethod.POST;
        String jiraDomain = "mad1x";
        String issueKey = "TEST-10";

        JiraIssueTransitionSetDto transitionSetDto = new JiraIssueTransitionSetDto();

        prepareMockActions(jiraDomain, method, responseType, null);

        assertDoesNotThrow(() -> jiraService.setStatusForIssue(jiraDomain, issueKey, transitionSetDto));

        verify(userContext, times(2)).getUserId();
        verify(userServiceClient, times(1)).getUserJiraInfo(anyLong(), anyString());
        verify(jiraClientConfig, times(1)).getJiraClient(anyString(), anyString(), anyString());
        verify(webClientErrorHandler, times(1)).handleResponse(any(ClientResponse.class), eq(responseType));
    }

    @Test
    void createIssueLinkTest() {
        Class<Void> responseType = Void.class;
        HttpMethod method = HttpMethod.POST;
        String jiraDomain = "mad1x";

        JiraIssueLinkCreateDto linkCreateDto = new JiraIssueLinkCreateDto();

        prepareMockActions(jiraDomain, method, responseType, null);

        assertDoesNotThrow(() -> jiraService.createIssueLink(jiraDomain, linkCreateDto));

        verify(userContext, times(2)).getUserId();
        verify(userServiceClient, times(1)).getUserJiraInfo(anyLong(), anyString());
        verify(jiraClientConfig, times(1)).getJiraClient(anyString(), anyString(), anyString());
        verify(webClientErrorHandler, times(1)).handleResponse(any(ClientResponse.class), eq(responseType));
    }

    @Test
    void deleteIssueLinkTest() {
        Class<Void> responseType = Void.class;
        HttpMethod method = HttpMethod.DELETE;
        String jiraDomain = "mad1x";
        String issueLinkId = "123";

        prepareMockActions(jiraDomain, method, responseType, null);

        assertDoesNotThrow(() -> jiraService.deleteIssueLink(jiraDomain, issueLinkId));

        verify(userContext, times(2)).getUserId();
        verify(userServiceClient, times(1)).getUserJiraInfo(anyLong(), anyString());
        verify(jiraClientConfig, times(1)).getJiraClient(anyString(), anyString(), anyString());
        verify(webClientErrorHandler, times(1)).handleResponse(any(ClientResponse.class), eq(responseType));
    }

    @Test
    void getIssueTest() {
        Class<JiraIssueDto> responseType = JiraIssueDto.class;
        JiraIssueDto response = new JiraIssueDto();
        HttpMethod method = HttpMethod.GET;
        String jiraDomain = "mad1x";
        String issueKey = "TEST-10";

        prepareMockActions(jiraDomain, method, responseType, response);

        assertDoesNotThrow(() -> jiraService.getIssue(jiraDomain, issueKey));

        verify(userContext, times(2)).getUserId();
        verify(userServiceClient, times(1)).getUserJiraInfo(anyLong(), anyString());
        verify(jiraClientConfig, times(1)).getJiraClient(anyString(), anyString(), anyString());
        verify(webClientErrorHandler, times(1)).handleResponse(any(ClientResponse.class), eq(responseType));
    }

    @Test
    void getAllDomainIssuesTest() {
        Class<JiraIssuesDto> responseType = JiraIssuesDto.class;
        JiraIssuesDto response = new JiraIssuesDto();
        response.setIssues(new ArrayList<>());
        HttpMethod method = HttpMethod.GET;
        String jiraDomain = "mad1x";

        prepareMockActions(jiraDomain, method, responseType, response);

        assertDoesNotThrow(() -> jiraService.getAllDomainIssues(jiraDomain));

        verify(userContext, times(2)).getUserId();
        verify(userServiceClient, times(1)).getUserJiraInfo(anyLong(), anyString());
        verify(jiraClientConfig, times(1)).getJiraClient(anyString(), anyString(), anyString());
        verify(webClientErrorHandler, times(1)).handleResponse(any(ClientResponse.class), eq(responseType));
    }

    @Test
    void filterDomainIssuesTest() {
        long userId = 2L;
        String userJiraEmail = "example@gmail.com";
        String userJiraToken = "901284v09182489124m9812984";

        Class<JiraIssuesDto> responseType = JiraIssuesDto.class;
        JiraIssuesDto response = new JiraIssuesDto();
        response.setIssues(new ArrayList<>());
        HttpMethod method = HttpMethod.GET;
        String jiraDomain = "mad1x";

        JiraIssueFilterDto filterDto = new JiraIssueFilterDto();
        filterDto.setProjectKey("TEST");
        filterDto.setAssigneeUsername("mad1x");
        filterDto.setStatus("To Do");

        UserJiraDto userJiraInfo = new UserJiraDto();
        userJiraInfo.setJiraEmail(userJiraEmail);
        userJiraInfo.setJiraToken(userJiraToken);

        WebClient jiraClient = mock(WebClient.class);
        WebClient.RequestBodyUriSpec requestUriSpec = mock(WebClient.RequestBodyUriSpec.class);
        WebClient.RequestBodySpec requestBodySpec = mock(WebClient.RequestBodySpec.class);

        when(userContext.getUserId()).thenReturn(userId);
        when(userServiceClient.getUserJiraInfo(userId, jiraDomain)).thenReturn(userJiraInfo);
        when(jiraClientConfig.getJiraClient(jiraDomain, userJiraEmail, userJiraToken)).thenReturn(jiraClient);
        when(jiraClient.method(method)).thenReturn(requestUriSpec);
        when(requestUriSpec.uri(any(String.class))).thenReturn(requestBodySpec);

        when(requestBodySpec.exchangeToMono(any()))
                .thenAnswer(invocation -> {
                    Function<ClientResponse, Mono<JiraIssuesDto>> handler = invocation.getArgument(0);
                    ClientResponse mockResponse = mock(ClientResponse.class);
                    return handler.apply(mockResponse);
                });
        when(webClientErrorHandler.handleResponse(any(ClientResponse.class), eq(responseType)))
                .thenAnswer(invocation -> Mono.just(response));

        assertDoesNotThrow(() -> jiraService.filterDomainIssues(jiraDomain, filterDto));

        verify(requestUriSpec).uri(uriCaptor.capture());
        assertTrue(uriCaptor.getValue().contains("%s?jql=".formatted(SEARCH_ENDPOINT)));
        assertTrue(uriCaptor.getValue().contains("project=TEST"));
        assertTrue(uriCaptor.getValue().contains("assignee='mad1x'"));
        assertTrue(uriCaptor.getValue().contains("status='To Do'"));
        verify(userContext, times(2)).getUserId();
        verify(userServiceClient, times(1)).getUserJiraInfo(anyLong(), anyString());
        verify(jiraClientConfig, times(1)).getJiraClient(anyString(), anyString(), anyString());
        verify(webClientErrorHandler, times(1)).handleResponse(any(ClientResponse.class), eq(responseType));
    }

    private <T> void prepareMockActions(String jiraDomain, HttpMethod method, Class<T> responseType, T responseObject) {
        long userId = 2L;
        String userJiraEmail = "example@gmail.com";
        String userJiraToken = "901284v09182489124m9812984";

        UserJiraDto userJiraInfo = new UserJiraDto();
        userJiraInfo.setJiraEmail(userJiraEmail);
        userJiraInfo.setJiraToken(userJiraToken);

        WebClient jiraClient = mock(WebClient.class);
        WebClient.RequestBodyUriSpec requestUriSpec = mock(WebClient.RequestBodyUriSpec.class);
        WebClient.RequestBodySpec requestBodySpec = mock(WebClient.RequestBodySpec.class);

        when(userContext.getUserId()).thenReturn(userId);
        when(userServiceClient.getUserJiraInfo(userId, jiraDomain)).thenReturn(userJiraInfo);
        when(jiraClientConfig.getJiraClient(jiraDomain, userJiraEmail, userJiraToken)).thenReturn(jiraClient);
        when(jiraClient.method(method)).thenReturn(requestUriSpec);
        when(requestUriSpec.uri(any(String.class))).thenReturn(requestBodySpec);

        when(requestBodySpec.exchangeToMono(any()))
                .thenAnswer(invocation -> {
                    Function<ClientResponse, Mono<T>> handler = invocation.getArgument(0);
                    ClientResponse mockResponse = mock(ClientResponse.class);
                    return handler.apply(mockResponse);
                });
        when(webClientErrorHandler.handleResponse(any(ClientResponse.class), eq(responseType)))
                .thenAnswer(invocation -> {
                    if (responseType == Void.class) {
                        return Mono.empty();
                    }
                    return Mono.just(responseObject);
                });
    }
}