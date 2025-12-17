package com.akash.embedqa.model.entities;

import com.akash.embedqa.converter.AuthConfigConverter;
import com.akash.embedqa.enums.AuthType;
import com.akash.embedqa.enums.BodyType;
import com.akash.embedqa.enums.HttpMethod;
import com.akash.embedqa.model.dtos.request.AuthConfigDTO;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: akash
 * Date: 29/10/25
 */

@Entity
@Table(name = "api_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiRequest extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String url;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HttpMethod method;

    @Column(columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy = "apiRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<RequestHeader> headers = new ArrayList<>();

    @OneToMany(mappedBy = "apiRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<QueryParameter> queryParams = new ArrayList<>();

    @Column(columnDefinition = "TEXT")
    private String requestBody;

    @Enumerated(EnumType.STRING)
    private BodyType bodyType;

    @Enumerated(EnumType.STRING)
    private AuthType authType;

    @Convert(converter = AuthConfigConverter.class)
    @Column(columnDefinition = "TEXT")
    private AuthConfigDTO authConfig;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collection_id")
    private ApiCollection collection;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "environment_id")
    private Environment environment;

    public void addHeader(RequestHeader header) {
        headers.add(header);
        header.setApiRequest(this);
    }

    public void addQueryParam(QueryParameter param) {
        queryParams.add(param);
        param.setApiRequest(this);
    }
}
