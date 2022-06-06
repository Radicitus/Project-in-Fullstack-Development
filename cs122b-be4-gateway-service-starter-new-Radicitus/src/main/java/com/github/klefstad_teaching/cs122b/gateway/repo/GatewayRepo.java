package com.github.klefstad_teaching.cs122b.gateway.repo;

import com.github.klefstad_teaching.cs122b.gateway.model.Container.GatewayRequestObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

@Component
public class GatewayRepo
{
    private final NamedParameterJdbcTemplate template;

    @Autowired
    public GatewayRepo(NamedParameterJdbcTemplate template)
    {
        this.template = template;
    }

    public Mono<int[]> insertRequests(List<GatewayRequestObject> requests)
    {
        return Mono.fromCallable(() -> insertRequestsHelper(requests));
    }

    public int[] insertRequestsHelper(List<GatewayRequestObject> requests) {
        MapSqlParameterSource[] arrayOfSources = createSources(requests).toArray(new MapSqlParameterSource[0]);

        return this.template.batchUpdate(
                "INSERT INTO gateway.request (ip_address, call_time, path) " +
                        "VALUES (:ip_address, :call_time, :path)",
                arrayOfSources
        );
    }

    public List<MapSqlParameterSource> createSources(List<GatewayRequestObject> requests) {
        List<MapSqlParameterSource> arrayOfSources = new ArrayList<>();

        for (GatewayRequestObject r : requests) {
            MapSqlParameterSource source = new MapSqlParameterSource()
                    .addValue("ip_address", r.getIpAddress(), Types.VARCHAR)
                    .addValue("call_time", Timestamp.from(r.getCallTime()), Types.TIMESTAMP)
                    .addValue("path", r.getPath(), Types.VARCHAR);

            arrayOfSources.add(source);
        }

        return arrayOfSources;
    }
}
