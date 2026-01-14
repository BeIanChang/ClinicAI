package ca.mcmaster.labtest.domain.model;

public record TestResultMetadata(
    String uri,
    String testType,
    String status,
    String doctorReport
) {}
