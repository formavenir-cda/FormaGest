import { Component, inject, computed } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { MatMenuModule } from '@angular/material/menu';
import { AuthService } from '../../services/auth/auth.service';
import { UserRole } from '../../models/users/user-role'

type SidebarNavItem = {
  label: string;
  icon: string;
  route?: string;
  roles?: UserRole[];
};

@Component({
  imports: [RouterLink, RouterLinkActive, MatMenuModule],
  selector: 'app-sidebar',
  styleUrl: './sidebar.scss',
  templateUrl: './sidebar.html',
})
export class Sidebar {
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);
  protected readonly user = toSignal(this.authService.user$);

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  protected readonly mainNavItems: SidebarNavItem[] = [
    { label: 'Accueil', icon: 'home', route: '/home' },
    { label: 'Mon calendrier',
      icon: 'calendar_month',
      roles: ['STUDENT']
    },
    { label: 'Mes cours',
      icon: 'menu_book',
      roles: ['STUDENT', 'TEACHER']
    },
    {
      label: 'Formation',
      icon: 'school',
      route: '/formation',
      roles: ['ADMINISTRATIVE_MANAGER'],
    },
    { label: 'Inscriptions',
      icon: 'how_to_reg',
      route: '/enrollments',
      roles: ['ADMINISTRATIVE_MANAGER']
    },
    {
      label: 'Promotions',
      icon: 'groups',
      route: '/cohorts',
      roles: ['ADMINISTRATIVE_MANAGER'],
    },
    { label: 'Élèves',
      icon: 'person_search',
      route: '/students',
      roles: ['ADMINISTRATIVE_MANAGER']
    },
    { label: 'Formateurs',
      icon: 'badge',
      route: '/teachers',
      roles: ['ADMINISTRATIVE_MANAGER']  },
    { label: 'Utilisateurs',
      icon: 'manage_accounts',
      route: '/users',
      roles: ['ADMINISTRATOR']
    },
  ];

  protected readonly visibleNavItems = computed(() => {
    const role = this.user()?.role;
    return this.mainNavItems.filter(item => !item.roles || (role && item.roles.includes(role)));
    }
  )
}
