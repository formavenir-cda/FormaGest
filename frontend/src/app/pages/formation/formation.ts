import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-formation',
  imports: [RouterLink, RouterLinkActive, RouterOutlet],
  templateUrl: './formation.html',
  styleUrl: './formation.scss',
})
export class Formation {}
