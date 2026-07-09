import { Component, OnInit, inject } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { EventService } from '../../../core/services/event.service';
import { ActivatedRoute, Router } from '@angular/router';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-form',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './form.html',
})
export class FormComponent implements OnInit {
  private fb = inject(FormBuilder);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private eventService = inject(EventService);
  isEditMode = false;

  form: FormGroup = this.fb.group({
    titulo: ['', Validators.required],
    local: ['', Validators.required],
    dataHora: ['', Validators.required],
    descricao: ['', Validators.required],
  });

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEditMode = true;
      this.eventService.getById(+id).subscribe((data) => {
        this.form.patchValue({
          titulo: data.titulo,
          local: data.local,
          descricao: data.descricao,
          dataHora: data.dataHora.slice(0, 16),
        });
      });
    }
  }

  save(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const eventData = this.form.value;
    const id = this.route.snapshot.paramMap.get('id');
    const page = this.route.snapshot.queryParamMap.get('page') || '0';
    const operation$ =
      this.isEditMode && id
        ? this.eventService.update(+id, eventData)
        : this.eventService.create(eventData);

    operation$.subscribe({
      next: () => {
        alert(this.isEditMode ? 'Evento atualizado com sucesso!' : 'Evento criado com sucesso!');
        this.router.navigate(['/events'], { queryParams: { page: page } });
      },
      error: (err: HttpErrorResponse) => {
        this.handleFormErrors(err);
      },
    });
  }

  private handleFormErrors(error: HttpErrorResponse): void {
    const errorData = error.error as { validacoes?: Record<string, string> };

    if (error.status === 400 && errorData.validacoes) {
      const erros = errorData.validacoes;
      Object.keys(erros).forEach((campo) => {
        const control = this.form.get(campo);
        if (control) {
          control.setErrors({ serverError: erros[campo] });
        }
      });
    } else {
      alert('Ocorreu um erro inesperado no servidor.');
    }
  }

  cancelar(): void {
    const page = this.route.snapshot.queryParamMap.get('page') || '0';
    this.router.navigate(['/events'], { queryParams: { page: page } });
  }
}
