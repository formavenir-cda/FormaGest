import { Component, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { AuthService } from '../../services/auth/auth.service';
import { Router, NavigationEnd } from '@angular/router';
import { filter, map, startWith } from 'rxjs';

@Component({
  selector: 'app-header',
  styleUrl: './header.scss',
  templateUrl: './header.html',
})
export class Header {
  private readonly authService = inject(AuthService);
  protected readonly user = toSignal(this.authService.user$);
  private readonly router = inject(Router);

  protected readonly pageTitle = toSignal(this.router.events.pipe(
    filter(e => e instanceof NavigationEnd),
    map(() => this.deepestTitle()),
    startWith(this.deepestTitle())
  ),
  );

  private deepestTitle(): string | undefined {
    let route = this.router.routerState.snapshot.root;
    while (route.firstChild) {
      route = route.firstChild;
    }
    return route.title;
  }
}
