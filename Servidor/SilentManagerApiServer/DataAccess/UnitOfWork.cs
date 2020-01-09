using SilentManager.API.DataAccess.Repositories;
using SilentManagerApiServer.Models.DB;
using System.Threading.Tasks;

namespace SilentManagerApiServer.DataAccess
{
    public class UnitOfWork : IUnitOfWork
    {
        private readonly SilentManagerContext _context;

        public IUserRepository UserRepo { get; private set; }
        public IEventRepository EventRepo { get; private set; }
        public IPersonalSettingsRepository PersonalSettingsRepo { get; private set; }
        public IInviteRepository InviteRepo { get; private set; }
        public ILocationRepository LocationRepo { get; private set; }
        public ITokenRepository tokenRepo { get; private set; }
        public IInviteStateRepository inviteStateRepo { get; private set; }
        public IEventHasCategoriesRepository eventHasCategories { get; private set; }
        public ICategoryRepository categoriesRepo { get; private set; }

        public UnitOfWork(SilentManagerContext context)
        {
            this._context = context;
            this.UserRepo = new UserRepository(_context);
            this.EventRepo = new EventRepository(_context);
            this.PersonalSettingsRepo = new PersonalSettingsRepository(_context);
            this.InviteRepo = new InviteRepository(_context);
            this.LocationRepo = new LocationRepository(_context);
            this.tokenRepo = new TokenRepository(_context);
            this.inviteStateRepo = new InviteStateRepository(_context);
            this.eventHasCategories = new EventHasCategoriesRepository(_context);
            this.categoriesRepo = new CategoryRepository(_context);
        }

        ///
        /// <summary>
        ///     Saves all changes made in this context to the database.
        /// </summary>
        ///
        /// <returns>
        ///     Return true if the data were saved or false if it doesn't
        /// </returns>
        ///
        public bool Complete()
        {
            return _context.SaveChanges() > 0;
        }

        public async Task<bool> CompleteAsync()
        {
            return await _context.SaveChangesAsync() > 0;
        }

        public void Dispose()
        {
            _context.Dispose();
        }
    }
}
