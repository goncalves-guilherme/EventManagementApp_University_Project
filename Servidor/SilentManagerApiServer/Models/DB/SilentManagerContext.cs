using System;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata;

namespace SilentManagerApiServer.Models.DB
{
    public partial class SilentManagerContext : DbContext
    {
        public SilentManagerContext()
        {
        }

        public SilentManagerContext(DbContextOptions<SilentManagerContext> options)
            : base(options)
        {
        }

        public virtual DbSet<Category> Category { get; set; }
        public virtual DbSet<Contact> Contact { get; set; }
        public virtual DbSet<Event> Event { get; set; }
        public virtual DbSet<EventHasCategories> EventHasCategories { get; set; }
        public virtual DbSet<Invite> Invite { get; set; }
        public virtual DbSet<InviteState> InviteState { get; set; }
        public virtual DbSet<Location> Location { get; set; }
        public virtual DbSet<PersonalSettings> PersonalSettings { get; set; }
        public virtual DbSet<SilentMode> SilentMode { get; set; }
        public virtual DbSet<StateEvent> StateEvent { get; set; }
        public virtual DbSet<Token> Token { get; set; }
        public virtual DbSet<User> User { get; set; }

        protected override void OnConfiguring(DbContextOptionsBuilder optionsBuilder)
        {
            if (!optionsBuilder.IsConfigured)
            {
#warning To protect potentially sensitive information in your connection string, you should move it out of source code. See http://go.microsoft.com/fwlink/?LinkId=723263 for guidance on storing connection strings.
                optionsBuilder.UseSqlServer("Server=DESKTOP-NDVUJ8H;Database=SilentManager;Trusted_Connection=True;");
            }
        }

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            modelBuilder.HasAnnotation("ProductVersion", "2.2.4-servicing-10062");

            modelBuilder.Entity<Category>(entity =>
            {
                entity.HasIndex(e => e.Category1)
                    .HasName("uc_category")
                    .IsUnique();

                entity.Property(e => e.CategoryId).HasColumnName("Category_Id");

                entity.Property(e => e.Category1)
                    .HasColumnName("Category")
                    .HasMaxLength(25)
                    .IsUnicode(false);
            });

            modelBuilder.Entity<Contact>(entity =>
            {
                entity.HasKey(e => e.PhoneNumber)
                    .HasName("pk_contact_phone_number");

                entity.Property(e => e.PhoneNumber)
                    .HasColumnName("Phone_Number")
                    .HasMaxLength(25)
                    .ValueGeneratedNever();

                entity.Property(e => e.Name).HasMaxLength(25);
            });

            modelBuilder.Entity<Event>(entity =>
            {
                entity.Property(e => e.EventId).HasColumnName("Event_Id");

                entity.Property(e => e.Description).HasMaxLength(25);

                entity.Property(e => e.EndDate)
                    .HasColumnName("End_Date")
                    .HasColumnType("datetime");

                entity.Property(e => e.Name).HasMaxLength(25);

                entity.Property(e => e.StartDate)
                    .HasColumnName("Start_Date")
                    .HasColumnType("datetime");

                entity.HasOne(d => d.AuthorNavigation)
                    .WithMany(p => p.Event)
                    .HasForeignKey(d => d.Author)
                    .HasConstraintName("fk_event_utilizador");

                entity.HasOne(d => d.LocationNavigation)
                    .WithMany(p => p.Event)
                    .HasForeignKey(d => d.Location)
                    .HasConstraintName("fk_event_localizacao");

                entity.HasOne(d => d.StateNavigation)
                    .WithMany(p => p.Event)
                    .HasForeignKey(d => d.State)
                    .HasConstraintName("fk_event_estado");
            });

            modelBuilder.Entity<EventHasCategories>(entity =>
            {
                entity.HasKey(e => new { e.EventId, e.CategoryId })
                    .HasName("pk_Event_Has_Categories");

                entity.ToTable("Event_Has_Categories");

                entity.Property(e => e.EventId).HasColumnName("Event_Id");

                entity.Property(e => e.CategoryId).HasColumnName("Category_Id");

                entity.HasOne(d => d.Category)
                    .WithMany(p => p.EventHasCategories)
                    .HasForeignKey(d => d.CategoryId)
                    .OnDelete(DeleteBehavior.ClientSetNull)
                    .HasConstraintName("fk_Event_Has_Categories_category_id");

                entity.HasOne(d => d.Event)
                    .WithMany(p => p.EventHasCategories)
                    .HasForeignKey(d => d.EventId)
                    .OnDelete(DeleteBehavior.ClientSetNull)
                    .HasConstraintName("fk_Event_Has_Categories_event_id");
            });

            modelBuilder.Entity<Invite>(entity =>
            {
                entity.HasKey(e => new { e.UserId, e.EventId })
                    .HasName("pk_invite");

                entity.Property(e => e.UserId).HasColumnName("User_Id");

                entity.Property(e => e.EventId).HasColumnName("Event_Id");

                entity.HasOne(d => d.Event)
                    .WithMany(p => p.Invite)
                    .HasForeignKey(d => d.EventId)
                    .OnDelete(DeleteBehavior.ClientSetNull)
                    .HasConstraintName("fk_invite_event");

                entity.HasOne(d => d.StateNavigation)
                    .WithMany(p => p.Invite)
                    .HasForeignKey(d => d.State)
                    .HasConstraintName("fk_invite_state");

                entity.HasOne(d => d.User)
                    .WithMany(p => p.Invite)
                    .HasForeignKey(d => d.UserId)
                    .OnDelete(DeleteBehavior.ClientSetNull)
                    .HasConstraintName("fk_invite_user");
            });

            modelBuilder.Entity<InviteState>(entity =>
            {
                entity.HasKey(e => e.InviteId)
                    .HasName("pk_invite_state");

                entity.ToTable("Invite_State");

                entity.HasIndex(e => e.State)
                    .HasName("uc_invite_state")
                    .IsUnique();

                entity.Property(e => e.InviteId).HasColumnName("Invite_Id");

                entity.Property(e => e.State)
                    .HasMaxLength(15)
                    .IsUnicode(false);
            });

            modelBuilder.Entity<Location>(entity =>
            {
                entity.HasIndex(e => e.Address)
                    .HasName("uc_location_address")
                    .IsUnique();

                entity.Property(e => e.LocationId).HasColumnName("Location_Id");

                entity.Property(e => e.Address)
                    .HasColumnName("address")
                    .HasMaxLength(25)
                    .IsUnicode(false);
            });

            modelBuilder.Entity<PersonalSettings>(entity =>
            {
                entity.HasKey(e => new { e.UserId, e.PhoneNumber })
                    .HasName("pk_personal_settings");

                entity.ToTable("Personal_Settings");

                entity.Property(e => e.UserId).HasColumnName("User_Id");

                entity.Property(e => e.PhoneNumber)
                    .HasColumnName("Phone_Number")
                    .HasMaxLength(25);

                entity.Property(e => e.SilentMode).HasColumnName("Silent_Mode");

                entity.HasOne(d => d.PhoneNumberNavigation)
                    .WithMany(p => p.PersonalSettings)
                    .HasForeignKey(d => d.PhoneNumber)
                    .OnDelete(DeleteBehavior.ClientSetNull)
                    .HasConstraintName("fk_personal_settings_contact");

                entity.HasOne(d => d.SilentModeNavigation)
                    .WithMany(p => p.PersonalSettings)
                    .HasForeignKey(d => d.SilentMode)
                    .HasConstraintName("fk_personal_settings_silent_mode");

                entity.HasOne(d => d.User)
                    .WithMany(p => p.PersonalSettings)
                    .HasForeignKey(d => d.UserId)
                    .OnDelete(DeleteBehavior.ClientSetNull)
                    .HasConstraintName("fk_personal_settings_user");
            });

            modelBuilder.Entity<SilentMode>(entity =>
            {
                entity.HasKey(e => e.ModeId)
                    .HasName("pk_silent_mode_mode_id");

                entity.ToTable("Silent_Mode");

                entity.Property(e => e.ModeId).HasColumnName("Mode_Id");

                entity.Property(e => e.Mode).HasMaxLength(25);
            });

            modelBuilder.Entity<StateEvent>(entity =>
            {
                entity.ToTable("State_Event");

                entity.HasIndex(e => e.State)
                    .HasName("uc_state_event_state")
                    .IsUnique();

                entity.Property(e => e.StateEventId).HasColumnName("State_Event_Id");

                entity.Property(e => e.State)
                    .HasMaxLength(25)
                    .IsUnicode(false);
            });

            modelBuilder.Entity<Token>(entity =>
            {
                entity.HasKey(e => e.token)
                    .HasName("pk_token");

                entity.Property(e => e.token)
                    .HasColumnName("token")
                    .HasMaxLength(32)
                    .ValueGeneratedNever();

                entity.Property(e => e.Expiration_Date)
                    .HasColumnName("expiration_Date")
                    .HasColumnType("datetime");

                entity.Property(e => e.UserId).HasColumnName("User_Id");

                entity.HasOne(d => d.User)
                    .WithMany(p => p.Token)
                    .HasForeignKey(d => d.UserId)
                    .HasConstraintName("fk_user");
            });

            modelBuilder.Entity<User>(entity =>
            {
                entity.HasIndex(e => e.Email)
                    .HasName("uc_user_email")
                    .IsUnique();

                entity.Property(e => e.UserId).HasColumnName("User_Id");

                entity.Property(e => e.CreateDate)
                    .HasColumnName("Create_Date")
                    .HasColumnType("datetime");

                entity.Property(e => e.Email)
                    .HasMaxLength(25)
                    .IsUnicode(false);

                entity.Property(e => e.Name).HasMaxLength(25);

                entity.Property(e => e.Password)
                    .HasMaxLength(125)
                    .IsUnicode(false);
            });
        }
    }
}
