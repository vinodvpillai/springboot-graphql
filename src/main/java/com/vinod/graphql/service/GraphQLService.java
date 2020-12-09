package com.vinod.graphql.service;

import com.vinod.graphql.model.Book;
import com.vinod.graphql.repository.BookRepository;
import com.vinod.graphql.service.datafetcher.AllBookDataFetcher;
import com.vinod.graphql.service.datafetcher.BookDataFetcher;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;


@Service
public class GraphQLService {

    @Value("classpath:books.graphql")
    private Resource resource;

    private GraphQL graphQL;
    @Autowired
    private AllBookDataFetcher allBooksDataFetcher;
    @Autowired
    private BookDataFetcher bookDataFetcher;
    @Autowired
    private BookRepository bookRepository;


    @PostConstruct
    private void loadSchema() throws IOException {
        //Load default data.
        loadDataIntoHSQL();
        //Get the schema
        File schemaFile=resource.getFile();
        //Parse schema
        TypeDefinitionRegistry typeDefinitionRegistry=new SchemaParser().parse(schemaFile);
        RuntimeWiring runtimeWiring=buildRuntimeWiring();
        GraphQLSchema graphQLSchema=new SchemaGenerator().makeExecutableSchema(typeDefinitionRegistry,runtimeWiring);
        graphQL = GraphQL.newGraphQL(graphQLSchema).build();
    }

    private void loadDataIntoHSQL() {
        Stream.of(
                Book.builder().isn("123").author(new String[] {"Herbert Schildt"}).title("Java - A Beginner’s Guide").publishedDate("20 Jan 2020").publisher("McGraw Hill Education").build(),
                Book.builder().isn("234").author(new String[] {"Kathy Sierra"}).title("Head First Java: A Brain-Friendly Guide").publishedDate("20 Jan 2020").publisher("O'Reilly Media").build()
        ).forEach(book -> {
            bookRepository.save(book);
        });
    }

    private RuntimeWiring buildRuntimeWiring() {
        return RuntimeWiring.newRuntimeWiring()
                .type("Query",typeWiring -> typeWiring
                                .dataFetcher("allBooks",allBooksDataFetcher)
                                .dataFetcher("book",bookDataFetcher))
                .build();
    }

    public GraphQL getGraphQL() {
        return graphQL;
    }

}
