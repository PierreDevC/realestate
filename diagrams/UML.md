classDiagram
    %% Controllers (REST API Layer)
    class PropertyController {
        +getProperties(filters): ResponseEntity~Page~PropertyDto~~
        +getPropertyById(id): ResponseEntity~PropertyDto~
        +createProperty(dto): ResponseEntity~PropertyDto~
        +updateProperty(id, dto): ResponseEntity~PropertyDto~
        +deleteProperty(id): ResponseEntity~Void~
        +searchPropertiesByLocation(lat, lng, radius): ResponseEntity~List~PropertyDto~~
        +getPropertyApplications(id): ResponseEntity~List~ApplicationDto~~
        +getPropertyReviews(id): ResponseEntity~List~ReviewDto~~
        +getPropertyMaintenanceRequests(id): ResponseEntity~List~MaintenanceRequestDto~~
    }
    
    class ManagerController {
        +getManagerProfile(managerId): ResponseEntity~ManagerDto~
        +updateManagerProfile(managerId, dto): ResponseEntity~ManagerDto~
        +getManagerProperties(managerId): ResponseEntity~List~PropertyDto~~
        +getManagerApplications(managerId): ResponseEntity~List~ApplicationDto~~
        +getManagerMaintenanceRequests(managerId): ResponseEntity~List~MaintenanceRequestDto~~
        +getDashboardStats(managerId): ResponseEntity~DashboardStatsDto~
    }
    
    class TenantController {
        +getTenantProfile(tenantId): ResponseEntity~TenantDto~
        +updateTenantProfile(tenantId, dto): ResponseEntity~TenantDto~
        +getTenantApplications(tenantId): ResponseEntity~List~ApplicationDto~~
        +getTenantLeases(tenantId): ResponseEntity~List~LeaseDto~~
        +getTenantPayments(tenantId): ResponseEntity~List~PaymentDto~~
        +submitPropertyReview(dto): ResponseEntity~ReviewDto~
        +submitMaintenanceRequest(dto): ResponseEntity~MaintenanceRequestDto~
    }
    
    class ApplicationController {
        +submitApplication(dto): ResponseEntity~ApplicationDto~
        +updateApplicationStatus(id, status): ResponseEntity~ApplicationDto~
        +getApplicationById(id): ResponseEntity~ApplicationDto~
        +withdrawApplication(id): ResponseEntity~ApplicationDto~
    }
    
    class LeaseController {
        +createLease(dto): ResponseEntity~LeaseDto~
        +getLeaseById(id): ResponseEntity~LeaseDto~
        +updateLease(id, dto): ResponseEntity~LeaseDto~
        +terminateLease(id): ResponseEntity~LeaseDto~
        +renewLease(id, dto): ResponseEntity~LeaseDto~
    }
    
    class PaymentController {
        +recordPayment(dto): ResponseEntity~PaymentDto~
        +getPaymentById(id): ResponseEntity~PaymentDto~
        +getLeasePayments(leaseId): ResponseEntity~List~PaymentDto~~
        +processRentPayment(leaseId, paymentDto): ResponseEntity~PaymentDto~
        +getOverduePayments(): ResponseEntity~List~PaymentDto~~
        +calculateLateFees(leaseId): ResponseEntity~PaymentDto~
    }
    
    class MaintenanceController {
        +createMaintenanceRequest(dto): ResponseEntity~MaintenanceRequestDto~
        +updateMaintenanceRequest(id, dto): ResponseEntity~MaintenanceRequestDto~
        +getMaintenanceRequest(id): ResponseEntity~MaintenanceRequestDto~
        +assignMaintenance(id, assignee): ResponseEntity~MaintenanceRequestDto~
        +completeMaintenanceRequest(id, dto): ResponseEntity~MaintenanceRequestDto~
    }

    %% Service Layer (Business Logic)
    class PropertyService {
        +findAllProperties(pageable, filters): Page~Property~
        +findPropertyById(id): Property
        +createProperty(property): Property
        +updateProperty(id, property): Property
        +deleteProperty(id): void
        +searchByRadius(lat, lng, radius): List~Property~
        +updateAverageRating(propertyId): void
        +markAsUnavailable(propertyId): void
        +getPropertiesByManager(managerClerkId): List~Property~
    }
    
    class ManagerService {
        +findManagerById(id): Manager
        +findManagerByClerkId(clerkId): Manager
        +createManager(manager): Manager
        +updateManager(id, manager): Manager
        +getManagerDashboardStats(managerClerkId): DashboardStats
        +getManagerProperties(managerClerkId): List~Property~
    }
    
    class TenantService {
        +findTenantById(id): Tenant
        +findTenantByClerkId(clerkId): Tenant
        +createTenant(tenant): Tenant
        +updateTenant(id, tenant): Tenant
        +getTenantActiveLeases(tenantClerkId): List~Lease~
    }
    
    class ApplicationService {
        +submitApplication(application): Application
        +updateApplicationStatus(id, status): Application
        +findApplicationById(id): Application
        +withdrawApplication(id): Application
        +findApplicationsByProperty(propertyId): List~Application~
        +findApplicationsByTenant(tenantClerkId): List~Application~
        +processApplicationApproval(applicationId): Lease
    }
    
    class LeaseService {
        +createLease(lease): Lease
        +findLeaseById(id): Lease
        +updateLease(id, lease): Lease
        +terminateLease(id, reason): Lease
        +renewLease(id, newEndDate): Lease
        +findActiveLeasesByTenant(tenantClerkId): List~Lease~
        +generateMonthlyPayments(leaseId): List~Payment~
        +isLeaseExpired(lease): boolean
    }
    
    class PaymentService {
        +recordPayment(payment): Payment
        +processRentPayment(leaseId, amount, method): Payment
        +findPaymentById(id): Payment
        +findPaymentsByLease(leaseId): List~Payment~
        +calculateOutstandingBalance(leaseId): BigDecimal
        +findOverduePayments(): List~Payment~
        +calculateLateFee(payment): BigDecimal
        +generateNextPayment(leaseId): Payment
    }
    
    class ReviewService {
        +createReview(review): PropertyReview
        +findReviewsByProperty(propertyId): List~PropertyReview~
        +findReviewsByTenant(tenantClerkId): List~PropertyReview~
        +calculatePropertyAverageRating(propertyId): Float
        +verifyReviewEligibility(tenantClerkId, propertyId): boolean
    }
    
    class MaintenanceService {
        +createMaintenanceRequest(request): MaintenanceRequest
        +updateMaintenanceRequest(id, request): MaintenanceRequest
        +findMaintenanceRequestById(id): MaintenanceRequest
        +assignMaintenanceRequest(id, assignee): MaintenanceRequest
        +completeMaintenanceRequest(id, actualCost): MaintenanceRequest
        +findRequestsByProperty(propertyId): List~MaintenanceRequest~
        +findRequestsByTenant(tenantClerkId): List~MaintenanceRequest~
        +findEmergencyRequests(): List~MaintenanceRequest~
    }
    
    class LocationService {
        +findLocationById(id): Location
        +createLocation(location): Location
        +geocodeAddress(address): Point
        +findLocationsByCity(city): List~Location~
    }
    
    class FileStorageService {
        +uploadPropertyImages(files, propertyId): List~String~
        +uploadMaintenancePhotos(files, requestId): List~String~
        +uploadLeaseDocument(file, leaseId): String
        +deleteFile(s3Key): void
        +generatePresignedUrl(s3Key): String
    }

    %% Repository Layer (Data Access)
    class PropertyRepository {
        +findAll(pageable): Page~Property~
        +findById(id): Optional~Property~
        +findByManagerClerkId(managerClerkId): List~Property~
        +findByIsAvailableTrue(): List~Property~
        +findByPricePerMonthBetween(min, max): List~Property~
        +findByPropertyType(type): List~Property~
        +findPropertiesWithinRadius(lat, lng, radius): List~Property~
        +findByBedsGreaterThanEqualAndBathsGreaterThanEqual(beds, baths): List~Property~
        +findByLocationCityAndLocationState(city, state): List~Property~
    }
    
    class ManagerRepository {
        +findById(id): Optional~Manager~
        +findByClerkId(clerkId): Optional~Manager~
        +findByEmail(email): Optional~Manager~
    }
    
    class TenantRepository {
        +findById(id): Optional~Tenant~
        +findByClerkId(clerkId): Optional~Tenant~
        +findByEmail(email): Optional~Tenant~
    }
    
    class ApplicationRepository {
        +findById(id): Optional~Application~
        +findByPropertyId(propertyId): List~Application~
        +findByTenantClerkId(tenantClerkId): List~Application~
        +findByStatus(status): List~Application~
        +findByPropertyIdAndTenantClerkId(propertyId, tenantClerkId): Optional~Application~
        +countByPropertyIdAndStatus(propertyId, status): Long
    }
    
    class LeaseRepository {
        +findById(id): Optional~Lease~
        +findByPropertyId(propertyId): List~Lease~
        +findByTenantClerkId(tenantClerkId): List~Lease~
        +findByLeaseStatus(status): List~Lease~
        +findActiveLeasesByTenant(tenantClerkId): List~Lease~
        +findExpiredLeases(): List~Lease~
        +findByEndDateBefore(date): List~Lease~
    }
    
    class PaymentRepository {
        +findById(id): Optional~Payment~
        +findByLeaseId(leaseId): List~Payment~
        +findByPaymentStatus(status): List~Payment~
        +findOverduePayments(): List~Payment~
        +findByDueDateBefore(date): List~Payment~
        +findByLeaseIdAndDueDateBetween(leaseId, start, end): List~Payment~
    }
    
    class PropertyReviewRepository {
        +findById(id): Optional~PropertyReview~
        +findByPropertyId(propertyId): List~PropertyReview~
        +findByTenantClerkId(tenantClerkId): List~PropertyReview~
        +findByPropertyIdAndIsVerifiedTrue(propertyId): List~PropertyReview~
        +findByPropertyIdAndTenantClerkId(propertyId, tenantClerkId): Optional~PropertyReview~
        +calculateAverageRatingByPropertyId(propertyId): Optional~Double~
    }
    
    class MaintenanceRequestRepository {
        +findById(id): Optional~MaintenanceRequest~
        +findByPropertyId(propertyId): List~MaintenanceRequest~
        +findByTenantClerkId(tenantClerkId): List~MaintenanceRequest~
        +findByStatus(status): List~MaintenanceRequest~
        +findByPriority(priority): List~MaintenanceRequest~
        +findEmergencyRequests(): List~MaintenanceRequest~
        +findByPropertyIdAndStatus(propertyId, status): List~MaintenanceRequest~
    }
    
    class LocationRepository {
        +findById(id): Optional~Location~
        +findByCity(city): List~Location~
        +findByPostalCode(postalCode): List~Location~
        +findByCityAndState(city, state): List~Location~
    }

    %% Entity Classes (Domain Models)
    class Property {
        +Long id
        +String name
        +String description
        +BigDecimal pricePerMonth
        +BigDecimal securityDeposit
        +BigDecimal applicationFee
        +String[] photoUrls
        +String[] amenities
        +String[] highlights
        +Boolean isPetsAllowed
        +Boolean isParkingIncluded
        +Integer beds
        +Float baths
        +Integer squareFeet
        +String propertyType
        +LocalDateTime postedDate
        +Float averageRating
        +Integer numberOfReviews
        +Boolean isAvailable
        +Location location
        +Manager manager
    }
    
    class Manager {
        +Long id
        +String clerkId
        +String name
        +String email
        +String phoneNumber
        +LocalDateTime createdAt
        +LocalDateTime updatedAt
    }
    
    class Tenant {
        +Long id
        +String clerkId
        +String name
        +String email
        +String phoneNumber
        +LocalDateTime createdAt
        +LocalDateTime updatedAt
    }
    
    class Application {
        +Long id
        +LocalDateTime applicationDate
        +ApplicationStatus status
        +Property property
        +Tenant tenant
        +String applicantName
        +String applicantEmail
        +String applicantPhone
        +String message
        +String leaseId
        +LocalDateTime createdAt
        +LocalDateTime updatedAt
    }
    
    class Lease {
        +Long id
        +LocalDate startDate
        +LocalDate endDate
        +BigDecimal monthlyRent
        +BigDecimal securityDeposit
        +LeaseStatus leaseStatus
        +Property property
        +Tenant tenant
        +Application application
        +String leaseDocumentUrl
        +LocalDateTime createdAt
        +LocalDateTime updatedAt
    }
    
    class Payment {
        +Long id
        +BigDecimal amountDue
        +BigDecimal amountPaid
        +LocalDate dueDate
        +LocalDateTime paymentDate
        +PaymentStatus paymentStatus
        +String paymentMethod
        +String paymentReference
        +BigDecimal lateFee
        +Lease lease
        +LocalDateTime createdAt
        +LocalDateTime updatedAt
    }
    
    class PropertyReview {
        +Long id
        +Integer rating
        +String reviewText
        +Property property
        +Tenant tenant
        +Lease lease
        +Boolean isVerified
        +LocalDateTime createdAt
        +LocalDateTime updatedAt
    }
    
    class MaintenanceRequest {
        +Long id
        +String title
        +String description
        +MaintenancePriority priority
        +MaintenanceStatus status
        +String category
        +Property property
        +Tenant tenant
        +String assignedTo
        +BigDecimal estimatedCost
        +BigDecimal actualCost
        +LocalDateTime scheduledDate
        +LocalDateTime completedDate
        +String[] photos
        +LocalDateTime createdAt
        +LocalDateTime updatedAt
    }
    
    class Location {
        +Long id
        +String address
        +String city
        +String state
        +String country
        +String postalCode
        +Point coordinates
        +LocalDateTime createdAt
        +LocalDateTime updatedAt
    }

    %% DTOs (Data Transfer Objects)
    class PropertyDto {
        +Long id
        +String name
        +String description
        +BigDecimal pricePerMonth
        +BigDecimal securityDeposit
        +BigDecimal applicationFee
        +String[] photoUrls
        +String[] amenities
        +String[] highlights
        +Boolean isPetsAllowed
        +Boolean isParkingIncluded
        +Integer beds
        +Float baths
        +Integer squareFeet
        +String propertyType
        +LocationDto location
        +ManagerDto manager
        +Float averageRating
        +Integer numberOfReviews
        +Boolean isAvailable
    }
    
    class ManagerDto {
        +Long id
        +String name
        +String email
        +String phoneNumber
    }
    
    class TenantDto {
        +Long id
        +String name
        +String email
        +String phoneNumber
    }
    
    class ApplicationDto {
        +Long id
        +LocalDateTime applicationDate
        +String status
        +PropertyDto property
        +TenantDto tenant
        +String applicantName
        +String applicantEmail
        +String applicantPhone
        +String message
    }
    
    class LeaseDto {
        +Long id
        +LocalDate startDate
        +LocalDate endDate
        +BigDecimal monthlyRent
        +BigDecimal securityDeposit
        +String leaseStatus
        +PropertyDto property
        +TenantDto tenant
        +String leaseDocumentUrl
    }
    
    class PaymentDto {
        +Long id
        +BigDecimal amountDue
        +BigDecimal amountPaid
        +LocalDate dueDate
        +LocalDateTime paymentDate
        +String paymentStatus
        +String paymentMethod
        +BigDecimal lateFee
        +LeaseDto lease
    }
    
    class ReviewDto {
        +Long id
        +Integer rating
        +String reviewText
        +PropertyDto property
        +TenantDto tenant
        +Boolean isVerified
        +LocalDateTime createdAt
    }
    
    class MaintenanceRequestDto {
        +Long id
        +String title
        +String description
        +String priority
        +String status
        +String category
        +PropertyDto property
        +TenantDto tenant
        +String assignedTo
        +BigDecimal estimatedCost
        +BigDecimal actualCost
        +LocalDateTime scheduledDate
        +LocalDateTime completedDate
        +String[] photos
    }
    
    class LocationDto {
        +String address
        +String city
        +String state
        +String country
        +String postalCode
        +Double latitude
        +Double longitude
    }
    
    class DashboardStatsDto {
        +Integer totalProperties
        +Integer occupiedProperties
        +Integer pendingApplications
        +BigDecimal monthlyRevenue
        +Integer maintenanceRequests
        +Float averageRating
    }

    %% Enums
    class ApplicationStatus {
        <<enumeration>>
        PENDING
        APPROVED
        REJECTED
        WITHDRAWN
        EXPIRED
    }
    
    class LeaseStatus {
        <<enumeration>>
        ACTIVE
        EXPIRED
        TERMINATED
        RENEWED
    }
    
    class PaymentStatus {
        <<enumeration>>
        PENDING
        PAID
        OVERDUE
        PARTIAL
        FAILED
        REFUNDED
    }
    
    class MaintenancePriority {
        <<enumeration>>
        LOW
        MEDIUM
        HIGH
        EMERGENCY
    }
    
    class MaintenanceStatus {
        <<enumeration>>
        OPEN
        IN_PROGRESS
        COMPLETED
        CANCELLED
        ON_HOLD
    }

    %% Configuration Classes
    class SecurityConfig {
        +filterChain(http): SecurityFilterChain
        +jwtDecoder(): JwtDecoder
        +corsConfigurationSource(): CorsConfigurationSource
        +clerkJwtAuthenticationConverter(): JwtAuthenticationConverter
    }
    
    class DatabaseConfig {
        +dataSource(): DataSource
        +entityManagerFactory(): LocalContainerEntityManagerFactoryBean
        +postgisDialect(): Dialect
        +transactionManager(): PlatformTransactionManager
    }
    
    class S3Config {
        +s3Client(): S3Client
        +bucketName: String
        +cloudFrontDomain: String
    }
    
    class SchedulingConfig {
        +taskScheduler(): TaskScheduler
        +overridePaymentChecker(): ScheduledAnnotationBeanPostProcessor
    }

    %% Relationships
    PropertyController --> PropertyService
    ManagerController --> ManagerService
    TenantController --> TenantService
    ApplicationController --> ApplicationService
    LeaseController --> LeaseService
    PaymentController --> PaymentService
    MaintenanceController --> MaintenanceService
    
    PropertyService --> PropertyRepository
    PropertyService --> ReviewService
    PropertyService --> LocationService
    ManagerService --> ManagerRepository
    TenantService --> TenantRepository
    ApplicationService --> ApplicationRepository
    LeaseService --> LeaseRepository
    PaymentService --> PaymentRepository
    ReviewService --> PropertyReviewRepository
    MaintenanceService --> MaintenanceRequestRepository
    LocationService --> LocationRepository
    
    Property --> Location
    Property --> Manager
    Application --> Property
    Application --> Tenant
    Lease --> Property
    Lease --> Tenant
    Lease --> Application
    Payment --> Lease
    PropertyReview --> Property
    PropertyReview --> Tenant
    PropertyReview --> Lease
    MaintenanceRequest --> Property
    MaintenanceRequest --> Tenant