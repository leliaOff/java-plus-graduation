package ru.practicum.controllers;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.practicum.ewm.stats.proto.RecommendationsControllerGrpc;
import ru.practicum.ewm.stats.proto.RecommendationsMessages;
import ru.practicum.models.RecommendedEvent;
import ru.practicum.services.RecommendationService;

import java.util.List;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class RecommendationsController extends RecommendationsControllerGrpc.RecommendationsControllerImplBase {
    private final RecommendationService service;

    @Override
    public void getSimilarEvents(RecommendationsMessages.SimilarEventsRequestProto request,
                                 StreamObserver<RecommendationsMessages.RecommendedEventProto> responseObserver) {
        try {
            List<RecommendedEvent> list = service.getSimilarEvents(request);
            for (RecommendedEvent re : list) {
                RecommendationsMessages.RecommendedEventProto proto = RecommendationsMessages.RecommendedEventProto.newBuilder()
                        .setEventId(re.eventId())
                        .setScore(re.score())
                        .build();
                responseObserver.onNext(proto);
            }
            responseObserver.onCompleted();
        } catch (IllegalArgumentException e) {
            log.error("Illegal Argument: {}", e.getMessage(), e);
            responseObserver.onError(
                    new StatusRuntimeException(Status.INVALID_ARGUMENT.withDescription(e.getMessage()).withCause(e))
            );
        } catch (Exception e) {
            log.error("Exception: {}", e.getMessage(), e);
            responseObserver.onError(
                    new StatusRuntimeException(Status.UNKNOWN.withDescription("Status Runtime").withCause(e))
            );
        }
    }

    @Override
    public void getRecommendationsForUser(RecommendationsMessages.UserPredictionsRequestProto request,
                                          StreamObserver<RecommendationsMessages.RecommendedEventProto> responseObserver) {
        try {
            List<RecommendedEvent> list = service.getRecommendationsForUser(request);
            for (RecommendedEvent re : list) {
                RecommendationsMessages.RecommendedEventProto proto = RecommendationsMessages.RecommendedEventProto.newBuilder()
                        .setEventId(re.eventId())
                        .setScore(re.score())
                        .build();
                responseObserver.onNext(proto);
            }
            responseObserver.onCompleted();
        } catch (IllegalArgumentException e) {
            log.error("Illegal Argument: {}", e.getMessage(), e);
            responseObserver.onError(
                    new StatusRuntimeException(Status.INVALID_ARGUMENT.withDescription(e.getMessage()).withCause(e))
            );
        } catch (Exception e) {
            log.error("Exception: {}", e.getMessage(), e);
            responseObserver.onError(
                    new StatusRuntimeException(Status.UNKNOWN.withDescription("Status Runtime").withCause(e))
            );
        }
    }

    @Override
    public void getInteractionsCount(RecommendationsMessages.InteractionsCountRequestProto request,
                                     StreamObserver<RecommendationsMessages.RecommendedEventProto> responseObserver) {
        try {
            List<RecommendedEvent> list = service.getInteractionsCount(request);
            for (RecommendedEvent re : list) {
                RecommendationsMessages.RecommendedEventProto proto = RecommendationsMessages.RecommendedEventProto.newBuilder()
                        .setEventId(re.eventId())
                        .setScore(re.score())
                        .build();
                responseObserver.onNext(proto);
            }
            responseObserver.onCompleted();
        } catch (IllegalArgumentException e) {
            log.error("Illegal Argument: {}", e.getMessage(), e);
            responseObserver.onError(
                    new StatusRuntimeException(Status.INVALID_ARGUMENT.withDescription(e.getMessage()).withCause(e))
            );
        } catch (Exception e) {
            log.error("Exception: {}", e.getMessage(), e);
            responseObserver.onError(
                    new StatusRuntimeException(Status.UNKNOWN.withDescription("Status Runtime").withCause(e))
            );
        }
    }
}
