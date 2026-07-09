import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { EventService } from '../../../core/services/event.service';
import { Evento } from '../../../core/models/evento.model';
import { ChangeDetectorRef } from '@angular/core';
import { RouterLink, ActivatedRoute } from '@angular/router';
import { ParamMap } from '@angular/router';

@Component({
  selector: 'app-list',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './list.html',
})
export class ListComponent implements OnInit {
  private eventService = inject(EventService);
  private cdr = inject(ChangeDetectorRef);
  private route = inject(ActivatedRoute);

  eventos: Evento[] = [];
  loading = true;
  page = 0;
  size = 10;
  totalPages = 0;
  sortColumn = 'dataHora';
  sortDirection = 'desc';

  ngOnInit(): void {
    this.size = Number(localStorage.getItem('pageSize')) || 10;
    this.route.queryParamMap.subscribe((params: ParamMap) => {
      const page = Number(params.get('page')) || 0;
      this.page = page;
      this.carregarEventos(this.page);
    });
  }

  carregarEventos(pageIndex: number): void {
    this.loading = true;
    const sortParam = `${this.sortColumn},${this.sortDirection}`;

    this.eventService.listar(pageIndex, this.size, sortParam).subscribe({
      next: (page) => {
        this.eventos = page.content;
        this.totalPages = page.totalPages;
        this.page = page.number;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Erro ao carregar', err);
        this.loading = false;
        this.cdr.detectChanges();
      },
    });
  }

  mudarOrdenacao(coluna: string): void {
    this.sortDirection =
      this.sortColumn === coluna && this.sortDirection === 'asc' ? 'desc' : 'asc';
    this.sortColumn = coluna;
    this.carregarEventos(0);
  }

  mudarTamanho(event: Event): void {
    const target = event.target as HTMLSelectElement;
    this.size = Number(target.value);
    localStorage.setItem('pageSize', target.value);
    this.page = 0;
    this.carregarEventos(this.page);
  }

  deletar(id: number): void {
    if (confirm('Tem certeza que deseja excluir este evento?')) {
      this.eventService.delete(id).subscribe({
        next: () => {
          this.eventos = this.eventos.filter((e) => e.id !== id);
          this.cdr.detectChanges();
        },
        error: (err) => {
          console.error('Erro ao excluir', err);
          alert('Erro ao excluir o evento.');
        },
      });
    }
  }
}
