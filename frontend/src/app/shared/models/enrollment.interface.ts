export interface EnrollmentRequest {
  courseId: number;
  fullName: string;
  email: string;
}

export interface EnrollmentResponse {
  id: number;
  courseId: number;
  courseTitle: string;
  studentName: string;
  studentEmail: string;
  enrollmentDate: string;
  availableSpots: number;
}

