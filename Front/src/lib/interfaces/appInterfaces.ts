interface Role {
    roleName: string;
}

interface User {
    id: number;
    name: string;
    email: string;
    roles: Array<Role>;
    createdAt: string;
}

export interface UsersPage {
    users: Array<User>;
    currentUsers: number;
    currentPage: number;
    totalUsers: number;
    totalPages: number;
}
