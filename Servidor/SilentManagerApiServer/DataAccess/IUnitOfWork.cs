using SilentManager.API.DataAccess.Repositories;
using SilentManagerApiServer.DataAccess;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace SilentManagerApiServer.DataAccess
{
    /**
     * Represents the contract between services and data layer.
     */
    public interface IUnitOfWork : IDisposable
    {
        /**
         * UserRepo constains operations to manipulate data from user's related data.
         */
        IUserRepository UserRepo { get; }
        /**
         * EventRepo constains operations to manipulate data from event's related data.
         */
        IEventRepository EventRepo { get; }
        /**
       * EventReo constains operations to manipulate data from event's related data.
       */
        IPersonalSettingsRepository PersonalSettingsRepo { get; }
        /**
         * Returns true if the data was persisted or false if nothing changed.
         */
        IInviteRepository InviteRepo { get; }

        ILocationRepository LocationRepo { get; }

        ITokenRepository tokenRepo { get; }

        IInviteStateRepository inviteStateRepo { get; }

        IEventHasCategoriesRepository eventHasCategories { get; }

        ICategoryRepository categoriesRepo { get; }

        bool Complete();
        /**
         * Returns asyncronosly true if the data was persisted or false if nothing changed.
         */

        ///
        /// <summary>
        ///     Saves all changes made in this context to the database asyncronously.
        /// </summary>
        ///
        /// <returns>
        ///     Return true if the data were saved or false if it doesn't
        /// </returns>
        ///
        Task<bool> CompleteAsync();
    }
}
