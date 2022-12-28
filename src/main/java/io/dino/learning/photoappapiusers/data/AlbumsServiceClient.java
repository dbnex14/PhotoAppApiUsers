package io.dino.learning.photoappapiusers.data;

import io.dino.learning.photoappapiusers.model.AlbumResponseModel;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;

// removed Hystrix Circuit Breaker since switching to Resilience4j
//@FeignClient(name = "albums-ws", fallbackFactory = AlbumsFallbackFactory.class)
@FeignClient(name = "albums-ws")
public interface AlbumsServiceClient {

    @GetMapping("/users/{id}/albums")
    @Retry(name = "albums-ws")
    @CircuitBreaker(name = "albums-ws", fallbackMethod = "getAlbumsFallback")
    public List<AlbumResponseModel> getAlbums(@PathVariable String id);

    default List<AlbumResponseModel> getAlbumsFallback(String id, Throwable exception) {
        System.out.println("Param = " + id);
        System.out.println("Exception took place: " + exception.getLocalizedMessage());
        return new ArrayList<>();
    }
}

// removed Hystrix Circuit Breaker since switching to Resilience4j
//@Component
//class AlbumsFallbackFactory implements FallbackFactory<AlbumsServiceClient> {
//    @Override
//    public AlbumsServiceClient create(Throwable cause) {
//        return new AlbumsServiceClientFallback(cause);
//    }
//}

// removed Hystrix Circuit Breaker since switching to Resilience4j
//class AlbumsServiceClientFallback implements AlbumsServiceClient {
//
//    Logger logger = LoggerFactory.getLogger(this.getClass());
//    private Throwable cause;
//
//    public AlbumsServiceClientFallback(Throwable cause) {
//        this.cause = cause;
//    }
//
//    // fallback method
//    @Override
//    public List<AlbumResponseModel> getAlbums(String id) {
//        if (cause instanceof FeignException && ((FeignException) cause).status() == 404) {
//            logger.error("404 error when getAlbums was called with userId: "
//                + id + ". Error message: " + cause.getLocalizedMessage());
//        }else {
//            logger.error("Other error occurred: " + cause.getLocalizedMessage());
//        }
//        return new ArrayList<>();
//    }
//}
