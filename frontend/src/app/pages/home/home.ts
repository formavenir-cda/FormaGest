import { Component, computed, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth/auth.service';
import { UserRole } from '../../models/users/user-role';

type QuickAccessCard = {
  label: string;
  icon: string;
  route: string;
  roles?: UserRole[];
};

@Component({
  imports: [RouterLink],
  selector: 'app-home',
  styleUrl: './home.scss',
  templateUrl: './home.html',
})
export class Home {
  private readonly authService = inject(AuthService);
  protected readonly user = toSignal(this.authService.user$);

  protected readonly quickAccessCards: QuickAccessCard[] = [
    { label: 'Formation', icon: 'school', route: '/formation', roles: ['ADMINISTRATIVE_MANAGER'] },
    { label: 'Utilisateurs', icon: 'manage_accounts', route: '/users', roles: ['ADMINISTRATOR'] },
  ];

  protected readonly visibleCards = computed(() => {
    const role = this.user()?.role;
    return this.quickAccessCards.filter((card) => !card.roles || (role && card.roles.includes(role)));
  });
}
