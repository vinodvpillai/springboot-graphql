package com.vinod.graphql.service.datafetcher;

import com.vinod.graphql.model.Book;
import com.vinod.graphql.repository.BookRepository;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class BookDataFetcher implements DataFetcher<Book> {
    @Autowired
    private BookRepository bookRepository;

    @Override
    public Book get(DataFetchingEnvironment dataFetchingEnvironment) {
        String isn = dataFetchingEnvironment.getArgument("id");
        Optional<Book> optionalBook=bookRepository.findById(isn);
        return optionalBook.isPresent() ? optionalBook.get() : null;
    }
}
