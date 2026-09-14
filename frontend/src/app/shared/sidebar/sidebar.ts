import { Component, inject } from '@angular/core';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { MatMenuModule } from '@angular/material/menu';
import { AuthService } from '../../services/auth/auth.service';

type SidebarNavItem = {
  label: string;
  icon: string;
  route?: string;
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

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  protected readonly mainNavItems: SidebarNavItem[] = [
    { label: 'Accueil', icon: 'home' },
    { label: 'Promotions', icon: 'groups' },
    { label: 'Mon calendrier', icon: 'calendar_month' },
    { label: 'Mes inscriptions', icon: 'assignment' },
    { label: 'Mes cours', icon: 'menu_book' },
    { label: 'Mes promotions', icon: 'school' },
    { label: 'Inscriptions', icon: 'how_to_reg' },
    { label: 'Eleves', icon: 'person_search' },
    { label: 'Filieres', icon: 'account_tree', route: '/filieres' },
    { label: 'Cursus', icon: 'route' },
    { label: 'Cours', icon: 'library_books' },
    { label: 'Formateurs', icon: 'badge' },
    { label: 'Utilisateurs', icon: 'manage_accounts' },
  ];
}
