import { Component, inject, OnInit, signal } from '@angular/core';
import { SectorService } from '../../services/training/sector.service';
import type { Sector } from '../../models/training/sector.model';
import { MatButtonModule } from '@angular/material/button';

@Component({
  imports: [MatButtonModule],
  selector: 'app-sectors',
  styleUrl: './sectors.scss',
  templateUrl: './sectors.html',
})
export class Sectors implements OnInit {
  private readonly sectorService = inject(SectorService);

  readonly sectors = signal<Sector[]>([]);
  readonly loading = signal(true);
  readonly error = signal('');

  ngOnInit(): void {
    this.sectorService.findAll().subscribe({
      next: (sectors) => {
        this.sectors.set(sectors);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Impossible de charger les filières.');
        this.loading.set(false);
      },
    });
  }
}
