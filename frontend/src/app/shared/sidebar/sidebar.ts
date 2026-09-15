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
    { label: 'Accueil', icon: 'home' },
    { label: 'Promotions', icon: 'groups' },
    { label: 'Mon calendrier', icon: 'calendar_month', roles: ['STUDENT'] },
    { label: 'Mes inscriptions', icon: 'assignment' },
    { label: 'Mes cours', icon: 'menu_book' },
    { label: 'Mes promotions', icon: 'school'},
    { label: 'Inscriptions', icon: 'how_to_reg', roles: ['ADMINISTRATIVE_MANAGER'] },
    { label: 'Eleves', icon: 'person_search' },
    { label: 'Filieres', icon: 'account_tree', route: '/filieres', roles: ['ADMINISTRATIVE_MANAGER'] },
    { label: 'Cursus', icon: 'route' },
    { label: 'Cours', icon: 'library_books' },
    { label: 'Formateurs', icon: 'badge' },
    { label: 'Utilisateurs', icon: 'manage_accounts', roles: ['ADMINISTRATOR'] },
  ];

  protected readonly visibleNavItems = computed(() => {
    const role = this.user()?.role;
    return this.mainNavItems.filter(item => !item.roles || (role && item.roles.includes(role)));
    }
  )
}
